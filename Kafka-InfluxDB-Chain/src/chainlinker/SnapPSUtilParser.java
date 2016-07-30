package chainlinker;

/*
 * Corresponding to snap-plugin-collector-psutil
 * 
 * CAUTION: This setting may fail in case if the plugins' version mismatch with the below.
 * - collector:psutil:6
 */
public class SnapPSUtilParser extends SnapPluginParser {
	public SnapPSUtilParser() {
		typeMap.put("/intel/psutil/load/load1", lfClass);
		typeMap.put("/intel/psutil/load/load5", lfClass);
		typeMap.put("/intel/psutil/load/load15", lfClass);
		typeMap.put("/intel/psutil/vm/free", lClass);
		typeMap.put("/intel/psutil/vm/used", lClass);
		typeMap.put("/intel/psutil/vm/available", lClass);		
	}		
}
