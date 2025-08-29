import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Table {

    public static void main(String[] args) {

        Table t = new Table("Information");

        t.addColumn("enrolled", "10");
        t.addColumn("absences", "00");
        t.addColumn("slaa", "00");
        t.addColumn("slaa", "00");
        t.addColumn("slaa", "00");
        t.addColumn("slaa", "00");

        t.addRown("2025.2");
        t.addRown("2025.1");



    }

    String name;
    List<Column> columns = new LinkedList<>();
    List<String> rows = new LinkedList<>();

    public Table(String name){
        this.name = name;
    }

    public void addColumn(String name, String value){

        Column column = null;
        boolean find = false;

        for(var c : columns){
            if(c.name.equals(name)){
                column = c;
                find = true;
                break;
            }
        }

        if(!find){
            column = new Column(name);
            columns.add(column);
        }

        column.add(value);

    }

    public void addRown(String name){
        rows.add(name);
    }

    public void addColumn(String name){
        columns.add(new Column(name));
    }

    public void addValueColumn(int index, String value){
        columns.get(index).values.add(value);
    }

    public String table(){

        String c = combineColumns();

        System.out.println(c);

        int max = maxSizeColumn();

        if(rows.size() != max){
            while (rows.size() < max){
                rows.add("-");
            }
        }

        int maxSizeRow = maxSizeRow();

        for(int i = -1; i < rows.size(); i++){

            if(i == -1){
                String current = line(" ", maxSizeRow);
                c = c.replace("#-", (current + " | "));
                continue;
            }

            String current = line(rows.get(i), maxSizeRow);


            c = c.replace("#" + i, current + "|");

        }

        int firstLineSize = c.split("\n")[0].length();

        return line(this.name, firstLineSize).replace(" ", "|") + "\n" + c;

    }

    public int maxSizeRow(){

        System.out.println("Aqui:" + rows );

        int max = 0;

        for(String r : rows){
            if(r.length() > max){
                max = r.length();
            }
        }

        return max;

    }


    public int maxSizeColumn(){
        int max = 0;
        for(var c : columns){
            if(c.values.size() > max){
                max = c.values.size();
            }
        }
        return max;
    }

    public String combineColumns(){

        List<Column> columns = this.columns;
        List<List<String>> lists = new LinkedList<>();

        int max = maxSizeColumn();

        for(var c : columns){
            if(c.size() <= max){
                while(c.size() != max){
                    c.add("-");
                }
            }
            lists.add(toList(c));
        }

        StringBuilder str = new StringBuilder();

        int i = 0;
        int j = 0;

        while(j <= max){

            if(i == 0){
                if(j == 0){
                    str.append("#").append("-");
                }else{
                str.append("#").append(j - 1);
                }
            }

            str.append(lists.get(i++).get(j));

            if(i == columns.size()){
                str.append("\n");
                i = 0;
                j++;
            }

        }

        return str.toString();
    }

    private List<String> toList(Column c){
        return new LinkedList<>(Arrays.stream(c.toString().replaceAll("#", "\n").split("\n")).toList());
    }

    public static class Column{
        String name;
        List<String> values = new LinkedList<>();

        public Column(String name) {
            this.name = name;
        }

        public void add(String value){
            values.add(value);
        }

        public String maxText(){

            int max = name.length();
            String m = name;
            for(var c : values){
                if(max < c.length()){
                    max = c.length();
                    m = c;
                }
            }
            return m;

        }

        @Override
        public String toString() {

            StringBuilder column = new StringBuilder();

            int max = maxText().length();

            if(max > name.length()){
                column.append(line(name, max));
            }else{
                column.append(name);
            }

            column.append(" ||#");

            for(var v : values){

                if(v.length() < max) column.append(line(v, max));
                else column.append(v);

                column.append(" ||#");

            }

            return column.toString();
        }

        public int size(){
            return values.size();
        }
    }

    public static String line(String value, int size){

        StringBuilder current = new StringBuilder();
        boolean aux = true;

        //mudei aqui
        while(current.length() < size){

            if( ((current.length() + value.length()/2) > (size/2)) && aux){
                aux = false;
                current.append(value);
            }

            current.append(" ");

        }

        return current.toString();
    }


}
