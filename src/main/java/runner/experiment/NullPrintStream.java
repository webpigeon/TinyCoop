package runner.experiment;

import java.io.IOException;
import java.io.OutputStream;

public class NullPrintStream extends OutputStream {

	@Override
	public void write(int b) throws IOException {

	}

}
