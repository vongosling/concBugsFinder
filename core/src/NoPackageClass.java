import org.checkthread.annotations.*;

// Used by unit tests
public class NoPackageClass {

	@ThreadConfined(ThreadName.MAIN)
	public void foo(){
	   bar();	// Thread bug here
	}
	
	@ThreadConfined(ThreadName.EDT)
	public void bar() {}	
}
