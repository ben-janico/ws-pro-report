function WorkingBlocks() {
  this.blocks = [];
  this.stopCount = 0;
  this.shortBlockCount = 0;
  this.calculate = function (slots) {
    this.blocks = [];
    for (var i = 0; i < slots.length; i++) {
      var slot = slots[i];
      var working = slot != null;
      var count = 1;
      while (i + 1 < slots.length && (slots[i + 1] != null) == working) {
        count++;
        i++;
      }
      this.blocks.push({
        working: working,
        count: count
      });
    }
    this.calculateStopCount();
    this.calculateShortBlocks();
  }
  this.calculateStopCount = function () {
    this.stopCount = 0;
    var usedOneIdle = false;
    for (var i = 1; i < this.blocks.length; i++) {
      var block = this.blocks[i];
      if (!block.working) {
        if (usedOneIdle) {
          this.stopCount++;
          usedOneIdle = false;
        } else {
          if (block.count == 1) {
            usedOneIdle = true;
          } else {
            this.stopCount++;
          }
        }
      }
    }
  }
  this.calculateShortBlocks = function () {
    this.shortBlockCount = 0;
    // start of the day and end of the day will not be counted. So skipping first and last block
    for (var i = 1; i < this.blocks.length - 1; i++) {
      var block = this.blocks[i];
      if (block.working && block.count < 6) {
        this.shortBlockCount++;
      }
    }
  }
}

function test(){
    //var service = new CrossoverService();
    //var data = service.collectData(Utils.getPreviousWorkDay(Utils.today()), Utils.today());
    Logger.log('test');
}
function CrossoverService() {
  function getOptions() {
    if (Constants.Crossover.XAuthToken) {
      return {
        'contentType': "application/json",
        'headers': {
          "X-Auth-Token": Constants.Crossover.XAuthToken
        },
        'muteHttpExceptions': true
      };
    } else {
      var encryptedCredentials = Utilities.base64Encode(Constants.Crossover.UserName + ":" + Constants.Crossover.Password);
      return {
        'contentType': "application/json",
        'headers': {
          "Authorization": "Basic " + encryptedCredentials
        },
        'muteHttpExceptions': true
      };
    }
  };

  function getRequestUrl(urlPath) {
    return encodeURI(Constants.Crossover.Url + urlPath);
  }

  function doGet(urlPath) {
    var requestUrl = getRequestUrl(urlPath);
    var options = getOptions();
    var response = UrlFetchApp.fetch(requestUrl, options);

    if (!response) {
      Utils.showMessage("Crossover Error", "Unable to make requests to Crossover API!");
      return null;
    }
    var responseCode = response.getResponseCode();
    switch (responseCode) {
      case 200:
        var result = JSON.parse(response.getContentText());
        return result;
      case 404:
        Utils.showMessage("Crossover Error", "No item found");
        return null;
      case 401:
        Utils.showMessage("Crossover Error", "Check username/password & permissions");
        return null;
      case 403:
        Utils.showMessage("Crossover Error", "Check username/password & permissions");
        return null;
      default:
        var data = JSON.parse(response.getContentText());
        Utils.showMessage("Crossover Error", data["text"]); // returns all errors that occured
        return null;
    }
  }

  function getTrackerData(date, teamId, managerId) {
    var url = Constants.Crossover.TrackerActivityGroupsTemplate
    .replace(Constants.Placeholders.Date, Utils.formatDate(date))
    .replace(Constants.Placeholders.TeamId, teamId)
    .replace(Constants.Placeholders.ManagerId, managerId);
    return doGet(url);
  };

  function getCurrentUserDetail() {
    var url = Constants.Crossover.IdentityUsersCurrrentDetailTemplate;
    return doGet(url);
  };

  function parseTracker(row) {
    var assignment = row.assignment;
    var candidate = assignment.candidate;
    var grouping = row.grouping;
    var devTimeRatio = 0;
    var developmentTime = 0;
    var otherTime = 0;
    var otherTimeRatio = 0.0;
    var chatTime = 0;
    var chatTimeRatio = 0.0;
    for (var i = 0; i < grouping.advancedGroups.length; i++) {
      var advancedGroup = grouping.advancedGroups[i];
      if (advancedGroup.sectionName === "Development") {
        developmentTime += advancedGroup.spentTime;
      }
      if (advancedGroup.sectionName === "Virtualization") {
        developmentTime += advancedGroup.spentTime;
      }
      if (advancedGroup.sectionName === "Other") {
        otherTime = advancedGroup.spentTime;
      }
      if (advancedGroup.sectionName === "Chat") {
        chatTime = advancedGroup.spentTime;
      }
    }
    if (grouping.totalTrackedTime > 0) {
      devTimeRatio = Math.round(developmentTime * 100 / grouping.totalTrackedTime);
      otherTimeRatio = Math.round(otherTime * 100 / grouping.totalTrackedTime);
      chatTimeRatio = Math.round(chatTime * 100 / grouping.totalTrackedTime);
    }
    var grouping = row.grouping;
    var blocks = new WorkingBlocks();
    blocks.calculate(row.dayActivitiesTime.contractorTimeSlots);
    return {
      id: candidate.id,
      name: candidate.printableName,
      sem: assignment.manager.printableName,
      sevenHours: grouping.periodLong >= 420,
      deepWorkBlocks: grouping.periodLong > 0 && blocks.stopCount <= 3,
      devTime70: devTimeRatio >= 70,
      intensityFocus: (grouping.focusScore + grouping.intensityScore)/2 >= 90,
      shortBlockCount: blocks.shortBlockCount,
      //
      focusScore: grouping.focusScore,
      intensityScore: grouping.intensityScore,
      deepWorkBlocksCount: blocks.stopCount,
      devTimeRatio: devTimeRatio,
      devTime: developmentTime,
      alignmentScore: grouping.alignmentScore,
      otherTimeRatio: otherTimeRatio,
      chatTimeRatio: chatTimeRatio,
      totalTime: grouping.periodLong,
      // not-displayed fields
      assignmentId: assignment.id
    };
  };

  function getCheckins(teamId, from, to) {
    var url = Constants.Crossover.ProductivityManagersCheckinsTemplate
    .replace(Constants.Placeholders.TeamId, teamId)
    .replace(Constants.Placeholders.From, Utils.formatDate(from))
    .replace(Constants.Placeholders.To, Utils.formatDate(to));
    return doGet(url);
  };

  function findCheckin(checkins, assignmentId, formattedDate) {
    if (checkins) {
      for (var i = 0; i < checkins.length; i++) {
        var checkin = checkins[i];
        if (checkin.assignmentId == assignmentId && checkin.date == formattedDate) {
          return checkin;
        }
      }
    }
    return null;
  };

  function isCheckinValid(ci) {
    if (ci && ci.comment) {
      var comment = ci.comment.toLocaleLowerCase('en-US');
      var keys = ['yesterday', 'today', 'blocker'];
      for (var i = 0; i < keys.length; i++) {
        if (comment.indexOf(keys[i]) < 0) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  this.collectData = function (from, to) {
    var user = getCurrentUserDetail();
    if (user != null) {
      var managerId = user.managerAvatar.id;
      var teamId = Constants.Team.BootcampTeamId;
      var checkins = getCheckins(teamId, from, to);
      var data = [];
      const dates = Utils.getDatesBetween(from, to);
      for (var i = 0; i < dates.length; i++) {
        var date = dates[i];
        var formattedDate = Utils.formatDate(date);
        var trackerData = getTrackerData(date, teamId, managerId);
        if (trackerData) {
          for (var k = 0; k < trackerData.length; k++) {
            var tracker = trackerData[k];
            var parsed = parseTracker(tracker);
            if(parsed.totalTime > 0) {
              parsed.date = formattedDate;
              var ci = findCheckin(checkins, parsed.assignmentId, formattedDate);
              if (ci) {
                parsed.checkinComment = ci.comment;
                parsed.checkin = true;
                parsed.complientCiC = isCheckinValid(ci);
              } else {
                parsed.checkinComment = null;
                parsed.checkin = false;
                parsed.complientCiC = false;
              }
              data.push(parsed);
            }
          }
        }
      }
    }
    return data;
  }

};
