package reciptizer.Common.Recipe;

import java.util.List;

public class Recipe {

    public Table1 table1;
    public List <Table2Row> rowsTable2;
    public List <Table3Row> rowsTable3;

    public Recipe(Table1 table1, List<Table2Row> rowsTable2, List<Table3Row> rowsTable3) {
        this.table1 = table1;
        this.rowsTable2 = rowsTable2;
        this.rowsTable3 = rowsTable3;
    }
}