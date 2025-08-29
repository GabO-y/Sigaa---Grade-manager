import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.lang.Float.NaN;

public class Discipline {

    String code;
    String name;
    List<Double> scores;
    int absence;
    Double avg;

    public Discipline(String code, String name, List<Double> scores, int absence, Double avg) {
        this.code = code;
        this.name = name;
        this.scores = scores;
        this.absence = absence;
        this.avg = avg;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        if(scores == null){
            sb.append("without grades");
        }else{

        for(var s : scores){
           sb.append(s).append(", ");
        }

        }

        sb.append("\b\b");

        return code + " " + name + " ( " + sb + " ): [ " + avg + " ] " + " | " + absence;
    }

    public double average(){

        if(scores == null) return  0.0;

        int count = 0;
        double sum = 0.0;
        int i = 4;

        for(var s : scores){
            sum += s * i;
            count += i++;
        }

        if(count == 0) return 0;

        return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(count), 1, RoundingMode.CEILING).doubleValue();
    }
}
