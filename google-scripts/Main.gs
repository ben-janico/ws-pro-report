function onOpen(e) {
  Main.onOpen();
}

function initialize() {
  Main.initialize();
}

function test(){
  // Main.printFormulas();
  var dev = 386;
  var tot = 552;
  var rat1 = dev * 100 / tot;
  var rat2 = Math.round(dev * 100 / tot);
  Logger.log('rat1: %s', rat1);
  Logger.log('rat2: %s', rat2);

}

var Main = {
  onOpen: function () {
    this.addHeader();
    this.addMenu();
    this.addConditionalFormattings();
  },
  initialize: function () {
    //this.loadMissingData();
  },
  addMenu: function () {
    var ui = SpreadsheetApp.getUi();
    var yesterdayStr='Load Yesterday : ' + Utils.formatDate(Utils.getPreviousWorkDay(Utils.today()));
    var thisWeekStr='Load This Week : '+ Utils.formatDate(Utils.getStartOfWeek(Utils.today()))+' - '+Utils.formatDate(Utils.today());
    var lastWeekStr;

    var today = Utils.today();
    var startOfLastWeek;
    var endOfLastWeek;
    if( Utils.isWorkDay(today)){
      var lastWeekToday = Utils.addDays(today,-7);
      startOfLastWeek = Utils.getStartOfWeek(lastWeekToday);
      endOfLastWeek = Utils.addDays(startOfLastWeek,6);
    }else{
      var testDay = Utils.addDays(today,-1);
      if(!Utils.isWorkDay(testDay)){
        testDay = Utils.addDays(testDay,-1);
      }
      startOfLastWeek = Utils.getStartOfWeek(testDay);
      endOfLastWeek = Utils.addDays(testDay,6);
    }

    lastWeekStr= 'Load Last Week :' + Utils.formatDate(startOfLastWeek)+' - '+Utils.formatDate(endOfLastWeek);

    ui.createMenu('WSPro Compliance')
      .addItem(yesterdayStr, 'loadDay')
      .addItem(thisWeekStr, 'loadWeek')
      .addItem(lastWeekStr, 'loadLastWeek')
      .addItem('Clear Yesterday', 'clearDay')
      .addItem('Clear This Week', 'clearWeek')
      .addItem('Clear All', 'clear')
      .addItem(Constants.HELP, 'showHelp')
      .addToUi();
  },
  addHeader: function () {
    var sheet = Sheets.getIndex();
    var range = sheet.getRange(1, 1, 1, Constants.IndexHeader.length)
    range.setValues([Constants.IndexHeader]);
  },
  loadMissingData: function () {
    var prevDay = Utils.getPreviousWorkDay(Utils.today());
    var day = Utils.addDays(prevDay, -1 * Constants.MaxMissingDaysToLoad);
    day = Utils.getStartOfWeek(day);
    while (day <= prevDay) {
      if (Utils.isWorkDay(day) && !this.hasValueForDate(day)) {
        loadByDate(day);
        break;
      }
      day = Utils.addDays(day, 1);
    }
  },
  addConditionalFormattings: function () {
    var sheet = Sheets.getIndex();
    var range1 = sheet.getRange("E:I");
    var range2 = sheet.getRange("K:K");
    var ruleYes = SpreadsheetApp.newConditionalFormatRule()
      .whenTextEqualTo(Constants.YES)
      .setBackground("#b7e1cd")
      .setRanges([range1, range2])
      .build();
    var ruleNo = SpreadsheetApp.newConditionalFormatRule()
      .whenTextEqualTo(Constants.NO)
      .setBackground("#f4c7c3")
      .setRanges([range1, range2])
      .build();
    var rules = [];
    rules.push(ruleYes);
    rules.push(ruleNo);
    rules.push(this.buildOtherConditionalRule());
    sheet.setConditionalFormatRules(rules);
  },
  buildOtherConditionalRule: function () {
    var sheet = Sheets.getIndex();
    var otherRange = sheet.getRange("S:S");
    return SpreadsheetApp.newConditionalFormatRule()
    .whenNumberGreaterThan(2)
    .setBackground("#f4c7c3")
    .setRanges([otherRange])
    .build();
  },
  asMatrix: function (data) {
    var result = [];
    for (var i = 0; i < data.length; i++) {
      var parsed = data[i];
      var rowIndex = i + 2;
      result.push([
        parsed.id,
        parsed.name,
        parsed.sem,
        parsed.date,
        parsed.sevenHours ? Constants.YES : Constants.NO,
        parsed.deepWorkBlocks ? Constants.YES : Constants.NO,
        parsed.devTime70 ? Constants.YES : Constants.NO,
        parsed.checkin ? Constants.YES : Constants.NO,
        parsed.intensityFocus ? Constants.YES : Constants.NO,
        parsed.shortBlockCount,
        parsed.complientCiC ? Constants.YES : Constants.NO,
        parsed.focusScore,
        parsed.intensityScore,
        parsed.deepWorkBlocksCount,
        parsed.devTimeRatio,
        parsed.devTime,
        parsed.totalTime,
        parsed.alignmentScore,
        parsed.otherTimeRatio,
        parsed.chatTimeRatio
      ]);
    }
    return result;
  },
  printData: function (data) {
    if (data && data.length > 0) {
      var sheet = Sheets.getIndex();
      var values = this.asMatrix(data);
      sheet.insertRowsAfter(1, values.length);
      var notes = [];
      for (var i = 0; i < data.length; i++) {
        notes.push([data[i].checkinComment]);
      }
      var range = sheet.getRange(2, 1, values.length, 20);
      range.setValues(values);
      var checkinRange = sheet.getRange(2, 8, notes.length, 1);
      checkinRange.setNotes(notes);
      this.printFormulas();
    }
  },
  printFormulas: function () {
    var sheet = Sheets.getIndex();
    var last = sheet.getLastRow();
    var formulas = [];
    for (var i = 2; i <= last; i++) {
      formulas.push(Formula.getScoreFormulas(i));
    }
    sheet.getRange(2, 21, formulas.length, 20).setValues(formulas);
  },
  hasValueForDate: function (date) {
    const formattedDate = Utils.formatDate(date);
    const sheet = Sheets.getIndex();
    var range = sheet.getRange("D2:D" + sheet.getLastRow());
    var values = range.getDisplayValues();
    for (var i = values.length - 1; i > -1; i--) {
      var day = values[i][0];
      if (day == formattedDate) {
        return true;
      }
    }
    return false;
  }
};

function loadDay() {
  var prevDay = Utils.getPreviousWorkDay(Utils.today());
  loadByDate(prevDay,Utils.today());
}

function loadWeek() {
  var startOfWeek = Utils.getStartOfWeek(Utils.today());
  loadByDate(startOfWeek,Utils.today());
}

function loadLastWeek() {
  var today = Utils.today();
  var startOfLastWeek;
  var endOfLastWeek;
  if( Utils.isWorkDay(today)){
    var lastWeekToday = Utils.addDays(today,-7);
    startOfLastWeek = Utils.getStartOfWeek(lastWeekToday);
    endOfLastWeek = Utils.addDays(startOfLastWeek,6);
  }else{
    var testDay = Utils.addDays(today,-1);
    if(!Utils.isWorkDay(testDay)){
      testDay = Utils.addDays(testDay,-1);
    }
    startOfLastWeek = Utils.getStartOfWeek(testDay);
    endOfLastWeek = Utils.addDays(testDay,6);
  }
  loadByDate(startOfLastWeek,endOfLastWeek);
}

function clearDay() {
  var prevDay = Utils.getPreviousWorkDay(Utils.today());
  clearByDate(prevDay);
}

function clearWeek() {
  var startOfWeek = Utils.getStartOfWeek(Utils.today());
    var dates = Utils.getDatesBetween(startOfWeek,Utils.today());
  for(var di=0;di<dates.length;di++){
    clearByDate(dates[di]);
  }
  clearByDate(startOfWeek);
}

function clearByDate(date) {
  const formattedDate = Utils.formatDate(date);
  const sheet = Sheets.getIndex();
  var range = sheet.getRange("D2:D" + sheet.getLastRow());
  var values = range.getDisplayValues();
  for (var i = values.length - 1; i > -1; i--) {
    var day = values[i][0];
    if (day == formattedDate) {
      sheet.deleteRow(i + 2);
    }
  }
};

function clear() {
  var sheet = Sheets.getIndex();
  sheet.deleteRows(2, sheet.getLastRow());
};

function loadByDate(start,end) {
  var dates = Utils.getDatesBetween(start,end);
  for(var di=0;di<dates.length;di++){
    clearByDate(dates[di]);
  }
  var service = new CrossoverService();
  var data = service.collectData(start, end);
  data.sort(function (a, b) {
    if (a.date > b.date) {
      return -1;
    } else if (a.date < b.date) {
      return 1;
    } else if (a.name < b.name) {
      return -1;
    } else if (a.name > b.name) {
      return 1;
    }
    return 0;
  });
  Main.printData(data);
}

function showHelp() {
  var html = HtmlService.createHtmlOutputFromFile(Constants.HELP)
    .setWidth(800)
    .setHeight(600);
  SpreadsheetApp.getUi()
    .showModalDialog(html, Constants.HELP);
}
