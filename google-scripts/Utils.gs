var Utils = {
  showMessage: function (title, message) {
    if (!Constants.IsSilentMode) {
      Browser.msgBox(title, message, Browser.Buttons.OK);
    }
  },
  formatDate: function (date) {
    return Utilities.formatDate(date, "GMT", "yyyy-MM-dd");
  },
  today: function () {
    return Utils.toUTC(new Date());
  },
  toUTC: function (date) {
    return new Date(Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), 0, 0, 0));
  },
  asDate: function (dateString) {
    const year = dateString.substring(0, 4);
    const month = dateString.substring(5, 7) - 1;
    const day = dateString.substring(8, 10);
    return new Date(Date.UTC(year, month, day, 0, 0, 0));
  },
  isWorkDay: function (date) {
    var day = date.getDay();
    if (day < 1 || day > 5)
      return false;

    return true;
  },
  getPreviousWorkDay: function (date) {
    var day = Utils.addDays(date, -1);
    while (!Utils.isWorkDay(day)) {
      day = Utils.addDays(day, -1);
    }
    return day;
  },
  addDays: function (date, daysToAdd) {
    var result = Utils.toUTC(new Date(date));
    result.setDate(date.getDate() + daysToAdd);
    return result;
  },
  getStartOfWeek: function (date) {
    var day = date.getDay() - 1;
    if (day == -1) {
      return Utils.addDays(date, -6);
    } else {
      return Utils.addDays(date, -1 * day);
    }
  },
  getDatesBetween: function (from, to) {
    var dates = [];
    var day = Utils.addDays(from, 0);
    while (day < to) {
      if (Utils.isWorkDay(day)) {
        dates.push(day);
      }
      day = Utils.addDays(day, 1);
    }
    return dates;
  },
}

Date.prototype.getWeekNumber = function () {
  var onejan = new Date(this.getFullYear(), 0, 1);
  return Math.ceil((((this - onejan) / 86400000) + onejan.getDay() + 1) / 7);
};

function testUtils(){
  var today = Utils.today();
  var formatted=Utils.formatDate(today);
  Logger.log('today: %s', today, Utils.isWorkDay(today));
  for(var i=0;i<10;i++){
    var day = Utils.addDays(today, -1*i);
    var sow = Utils.getStartOfWeek(day);
    Logger.log('Day: %s, isWorkDay: %s, start of week: %s', Utils.formatDate(day), Utils.isWorkDay(day), Utils.formatDate(sow));
  }
}
