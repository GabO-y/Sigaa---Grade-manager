import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.NaN;


public class Semester {

    String date;
    List<Discipline> disciplines = new ArrayList<>();

    public Semester(String date) {
        this.date = date;
    }

    public Semester() {
    }

    @Override
    public String toString() {

        clear();

        StringBuilder sb = new StringBuilder();

        for(var d : disciplines){
            sb.append(d).append("\n");
        }

        return String.format("""
            Date: %s
            Disciplines: %s
            """, date, sb);
    }

    public void clear(){
        disciplines.removeIf(d -> d.code == null || d.code.isBlank());
    }

    public Double ira(){

        int count = 0;
        double sum = 0;

        for(var d : disciplines){

            if(d.scores != null){

                int j = 4;

                for(int i = 0; i < d.scores.size(); i++){
                    sum += j * d.scores.get(i);
                    count += j++;
                }

            }

        }

        if(count == 0) return NaN;

        return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(count), 2, RoundingMode.CEILING).doubleValue();
    }

    public Integer totalEnrolled(){
        return disciplines.size();
    }

    public Integer totalAbsences(){

        int absences = 0;

        for(var d : disciplines){
            absences += d.absence;
        }
        return absences;
    }

}
