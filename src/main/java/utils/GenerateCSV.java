package utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by jwalto on 15/06/2015.
 */
public class GenerateCSV {
    private static final String DELIMITER = ",";
    private PrintStream fout;

    public GenerateCSV(String filename) throws FileNotFoundException {
        this.fout = new PrintStream(new FileOutputStream(filename, true));
    }

    public void writeLine(Object ... values) {
        StringBuilder buff = new StringBuilder();

        for (int i=0; i<values.length; i++) {
        	if (values[i] != null) {
        		String rawValue = values[i].toString();
        		buff.append(rawValue.replaceAll(",", ";"));
        	} else {
        		buff.append("NULL");
        	}
            if (i != values.length-1) {
                buff.append(DELIMITER);
            }
        }

        fout.println(buff.toString());
    }

    public void close() {
        fout.close();
    }

}
