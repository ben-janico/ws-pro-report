var Sheets = {
    Index: "index",
    ScoreWeights: "Score-Weights",
    sheets: [],
    getIndex: function () {
        return this.get(this.Index);
    },
    getScoreWeights: function () {
        return this.get(this.ScoreWeights);
    },
    get: function (name) {
        var ss = this.getSpreadSheet();
        var sheet = this.sheets[name];
        if (!sheet) {
            sheet = ss.getSheetByName(name);
        }
        this.sheets[name] = sheet;
        return sheet;
    },
    getSpreadSheet: function () {
        if (this.spreadsheet == null) {
            this.spreadsheet = SpreadsheetApp.getActiveSpreadsheet();
        }
        return this.spreadsheet;
    }
}
