package org.checkthread.main;

import org.checkthread.policy.*;
import org.checkthread.deadlockdetection.*;
import org.checkthread.xmlpolicy.*;

/**
 * Manages global cached state
 */
public class GlobalCacheManager {

	public static void clearCache() {
		PolicyFactory.clearCache();
        NodeLockFactory.clearCache();
        NodeMethodFactory.clearCache();
        LockAdjacencyListManager.clearCache();
        ThreadPolicyFromXML.clearCache();
	}

}
