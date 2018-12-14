var Formula = {
    TotalScore: function (rowIndex) {
        var formula = '=ROUND(IF(E%s = "Yes",\'Score-Weights\'!$B$2,0)';
        formula += '+ IF(F%s = "Yes",\'Score-Weights\'!$B$3,0)';
        formula += '+ IF(G%s = "Yes",\'Score-Weights\'!$B$4,0)';
        formula += '+ IF(H%s = "Yes",\'Score-Weights\'!$B$5,0)';
        formula += '+ IF(I%s = "Yes",\'Score-Weights\'!$B$6,0)';
        formula += '- J%s*\'Score-Weights\'!$B$7';
        formula += '+ IF(K%s = "Yes",\'Score-Weights\'!$B$8,0)';
        formula += '+ M%s*\'Score-Weights\'!$B$9';
        formula += '+ N%s*\'Score-Weights\'!$B$10';
        formula += '+ O%s*\'Score-Weights\'!$B$11';
        formula += '+ P%s*\'Score-Weights\'!$B$12';
        formula += ', 2)';
        return Utilities.formatString(formula, rowIndex, rowIndex, rowIndex, rowIndex, rowIndex, rowIndex, rowIndex, rowIndex, rowIndex, rowIndex, rowIndex);
    },
    getScoreFormulas: function (rowIndex) {
        var formulas = [];
        formulas.push(Utilities.formatString('=SUM(V%s:Z%s)-AA%s+SUM(AB%s:AH%s)', rowIndex, rowIndex, rowIndex, rowIndex, rowIndex));
        formulas.push(Utilities.formatString('=ROUND(IF(E%s = "Yes",\'Score-Weights\'!$B$2,0),2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(IF(F%s = "Yes",\'Score-Weights\'!$B$3,0),2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(IF(G%s = "Yes",\'Score-Weights\'!$B$4,0),2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(IF(H%s = "Yes",\'Score-Weights\'!$B$5,0),2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(IF(I%s = "Yes",\'Score-Weights\'!$B$6,0),2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(J%s*\'Score-Weights\'!$B$7,2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(IF(K%s = "Yes",\'Score-Weights\'!$B$8,0),2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(L%s*\'Score-Weights\'!$B$9,2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(M%s*\'Score-Weights\'!$B$10,2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(N%s*\'Score-Weights\'!$B$11,2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(O%s*\'Score-Weights\'!$B$12,2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(P%s*\'Score-Weights\'!$B$13,2)', rowIndex));
        formulas.push(Utilities.formatString('=ROUND(Q%s*\'Score-Weights\'!$B$14,2)', rowIndex));
        formulas.push(Utilities.formatString('=IF(E%s = "Yes",1,0)', rowIndex));
        formulas.push(Utilities.formatString('=IF(F%s = "Yes",1,0)', rowIndex));
        formulas.push(Utilities.formatString('=IF(G%s = "Yes",1,0)', rowIndex));
        formulas.push(Utilities.formatString('=IF(H%s = "Yes",1,0)', rowIndex));
        formulas.push(Utilities.formatString('=IF(K%s = "Yes",1,0)', rowIndex));
        formulas.push(Utilities.formatString('=IF(I%s = "Yes",1,0)', rowIndex));
        return formulas;
    },
    test: function(){
        return "=B4+B5";
    }
};
