import java.util.*;

public class Table {

    public static int maxSizeColumn = 0;

    public Table(String name){
        this.name = name;
    }

    List<Column> columns = new LinkedList<>();
    List<String> rows = new LinkedList<>();
    String name;

    public void addR(String name){
        rows.add(name);
    }

    public void addC(String name, String value){
        Column c = haveColumn(name);
        if(c == null){
            columns.add(new Column(name));
            columns.get(columns.size() - 1).add(value);
            return;
        }
        c.add(value);
    }

    public String table(){

        //gambirra pra duplicar a primeira coluna, pq ele oculta e nao sei como consertar
        Column col = columns.get(0);
        columns.add(0, new Column(col.name));

        for(var v : col.values){
            columns.get(0).add(v);
        }
        //fim da gambiarra

        normalizeColumns();

        List<List<String>> lines = new LinkedList<>();

        lines.add(new LinkedList<>());

        int first = columns.get(0).toString().split("#").length;

        while (first-- != 1){
            lines.add(new LinkedList<>());
        }

        for(var c : columns){

            String[] l = line(c.toString()).split("#");

            int i = 0;

            for(var line : l){
                lines.get(i++).add(line);
            }

        }

        String[] rows = line(organizeRows()).split("#");
        int i = 0;

        for(var r : rows){
           lines.get(i++).set(0, r);
        }

        //makeRows(lines);

        String table = listToTable(lines);

        return title(table.split("\n")[0].length()).replace(" ", "|") + "\n" + table;

    }

    public void makeRows(List<List<String>> lines){
        String[] rows = line(organizeRows()).split("#");
        int i = 0;

        for(var r : rows){
            lines.get(i++).set(0, r);
        }
    }

    public String title(int size){

        if(name.length() >= size){
            return name;
        }

        int leftSpaces = 0;
        int rightSpaces = 0;

        while(leftSpaces + rightSpaces + name.length() < size){
            leftSpaces++;
            rightSpaces++;
        }

        while(leftSpaces + rightSpaces + name.length() > size) leftSpaces--;

        return spaces(leftSpaces).replace(" ", "|") + name + spaces(rightSpaces - 1).replace(" ", "|");
    }

    public String organizeRows(){
        StringBuilder sb = new StringBuilder(" #");

        if(rows.size() < maxSizeColumn){
            while(rows.size() < maxSizeColumn){
                rows.add("-");
            }
        }

        for(var r : rows){
            sb.append(r).append("#");
        }

        return (sb + "#").replace("##", "");
    }

    public void normalizeColumns(){

        int max = 0;

        for(var c : columns){
            if(c.values.size() > max){
                max = c.values.size();
            }
        }
        maxSizeColumn = max;

        for(var c : columns){
            if(c.values.size() < max){
                while(c.values.size() < max){
                    addC(c.name, "-");
                }
            }
        }

    }

    public String listToTable(List<List<String>> list){

        StringBuilder sb = new StringBuilder();

        for(var l : list){
            for(var j : l){
                sb.append(j);
            }
            sb.append("\n");
        }

        return sb.toString();

    }

    public Column haveColumn(String name){
        for(var c : this.columns){
            if(c.name.equals(name)) return c;
        }
        return null;
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

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Column column = (Column) o;
            return Objects.equals(name, column.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();

            sb.append(name).append("#");

            for(var v : values){
                sb.append(v).append("#");
            }

            return (sb + "#").replace("##", "");
        }
    }

    //ex: value#value#value
    public static String line(String line){

        StringBuilder str = new StringBuilder();

        String[] fields = line.split("#");

        int max = 0;
        for(var field : fields){
            if(field.length() > max){
                max = field.length();
            }
        }

        for(var f : fields){

            int leftSpaces = 0;
            int rightSpaces = 0;

            if(f.length() < max){

                int totalSize = leftSpaces + f.length() + rightSpaces;

                while(totalSize < max){
                    leftSpaces++;
                    rightSpaces++;
                    totalSize = leftSpaces + f.length() + rightSpaces;
                }

                if(totalSize > max){
                    while(totalSize != max){
                        leftSpaces--;
                        totalSize = leftSpaces + f.length() + rightSpaces;
                    }
                }

                str.append(spaces(leftSpaces)).append(f).append(spaces(rightSpaces)).append(" | #");
            }else{
                str.append(f).append(" | #");
            }

        }
        return str.toString();
    }

    public static String spaces(int quant){
        StringBuilder spaces = new StringBuilder();
        while(spaces.length() != quant){
            spaces.append(" ");
        }
        return spaces.toString();
    }

}
