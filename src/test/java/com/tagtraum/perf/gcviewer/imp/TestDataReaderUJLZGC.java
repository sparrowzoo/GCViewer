package com.tagtraum.perf.gcviewer.imp;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import org.junit.Test;

/**
 * Test unified java logging ZGC algorithm in OpenJDK 11
 */
public class TestDataReaderUJLZGC {
    private GCModel getGCModelFromLogFile(String fileName) throws IOException {
        return UnittestHelper.getGCModelFromLogFile(fileName, UnittestHelper.FOLDER.OPENJDK_UJL, DataReaderUnifiedJvmLogging.class);
    }

    @Test
    public void testGcAll() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-zgc-gc-all.txt");
        assertThat("size", model.size(), is(11));
        assertThat("amount of gc event types", model.getGcEventPauses().size(), is(0));
        assertThat("amount of gc events", model.getGCPause().getN(), is(0));
        assertThat("amount of full gc event types", model.getFullGcEventPauses().size(), is(4));
        assertThat("amount of full gc events", model.getFullGCPause().getN(), is(4));
        assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), is(7));

        AbstractGCEvent<?> pauseMarkStartEvent = model.get(0);
        UnittestHelper.testMemoryPauseEvent(pauseMarkStartEvent,
                "Pause Mark Start",
                AbstractGCEvent.Type.UJL_ZGC_PAUSE_MARK_START,
                0.001279,
                0, 0, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> concurrentMarkEvent = model.get(1);
        UnittestHelper.testMemoryPauseEvent(concurrentMarkEvent,
                "Concurrent Mark",
                AbstractGCEvent.Type.UJL_ZGC_CONCURRENT_MARK,
                0.005216,
                0, 0, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> pauseMarkEndEvent = model.get(2);
        UnittestHelper.testMemoryPauseEvent(pauseMarkEndEvent,
                "Pause Mark End",
                AbstractGCEvent.Type.UJL_ZGC_PAUSE_MARK_END,
                0.000695,
                0, 0, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> concurrentNonrefEvent = model.get(3);
        UnittestHelper.testMemoryPauseEvent(concurrentNonrefEvent,
                "Concurrent Process Non-Strong References",
                AbstractGCEvent.Type.UJL_ZGC_CONCURRENT_NONREF,
                0.000258,
                0, 0, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> concurrentResetRelocSetEvent = model.get(4);
        UnittestHelper.testMemoryPauseEvent(concurrentResetRelocSetEvent,
                "Concurrent Reset Relocation Set",
                AbstractGCEvent.Type.UJL_ZGC_CONCURRENT_RESET_RELOC_SET,
                0.000001,
                0, 0, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> concurrentDetachedPagesEvent = model.get(5);
        UnittestHelper.testMemoryPauseEvent(concurrentDetachedPagesEvent,
                "Concurrent Destroy Detached Pages",
                AbstractGCEvent.Type.UJL_ZGC_CONCURRENT_DETATCHED_PAGES,
                0.000001,
                0, 0, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> concurrentSelectRelocSetEvent = model.get(6);
        UnittestHelper.testMemoryPauseEvent(concurrentSelectRelocSetEvent,
                "Concurrent Select Relocation Set",
                AbstractGCEvent.Type.UJL_ZGC_CONCURRENT_SELECT_RELOC_SET,
                0.003822,
                0, 0, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> concurrentPrepareRelocSetEvent = model.get(7);
        UnittestHelper.testMemoryPauseEvent(concurrentPrepareRelocSetEvent,
                "Concurrent Prepare Relocation Set",
                AbstractGCEvent.Type.UJL_ZGC_CONCURRENT_PREPARE_RELOC_SET,
                0.000865,
                0, 0, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> pauseRelocateStartEvent = model.get(8);
        UnittestHelper.testMemoryPauseEvent(pauseRelocateStartEvent,
                "Pause Relocate Start",
                AbstractGCEvent.Type.UJL_ZGC_PAUSE_RELOCATE_START,
                0.000679,
                0, 0, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> concurrentRelocateEvent = model.get(9);
        UnittestHelper.testMemoryPauseEvent(concurrentRelocateEvent,
                "Concurrent Relocate",
                AbstractGCEvent.Type.UJL_ZGC_CONCURRENT_RELOCATE,
                0.002846,
                0, 0, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> garbageCollectionEvent = model.get(10);
        UnittestHelper.testMemoryPauseEvent(garbageCollectionEvent,
                "Garbage Collection",
                AbstractGCEvent.Type.UJL_ZGC_GARBAGE_COLLECTION_METADATA_GC_THRESHOLD,
                0,
                1024 * 106, 1024 * 88, 1024 * 194560,
                AbstractGCEvent.Generation.ALL,
                true);
    }

    @Test
    public void testGcDefault() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-zgc-gc-default.txt");
        assertThat("size", model.size(), is(5));
        assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), is(0));
        assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), is(5));
        assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), is(0));

        // Default gc log gives no pause time or total heap size
        AbstractGCEvent<?> metadataGcThresholdEvent = model.get(0);
        UnittestHelper.testMemoryPauseEvent(metadataGcThresholdEvent,
                "Metadata GC Threshold heap",
                AbstractGCEvent.Type.UJL_ZGC_GARBAGE_COLLECTION_METADATA_GC_THRESHOLD,
                0,
                1024 * 106, 1024 * 88, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> warmupEvent = model.get(1);
        UnittestHelper.testMemoryPauseEvent(warmupEvent,
                "Warmup heap",
                AbstractGCEvent.Type.UJL_ZGC_GARBAGE_COLLECTION_WARMUP,
                0,
                1024 * 208, 1024 * 164, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> proactiveEvent = model.get(2);
        UnittestHelper.testMemoryPauseEvent(proactiveEvent,
                "Proactive heap",
                AbstractGCEvent.Type.UJL_ZGC_GARBAGE_COLLECTION_PROACTIVE,
                0,
                1024 * 19804, 1024 * 20212, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> allocationRateEvent = model.get(3);
        UnittestHelper.testMemoryPauseEvent(allocationRateEvent,
                "Allocation Rate heap",
                AbstractGCEvent.Type.UJL_ZGC_GARBAGE_COLLECTION_ALLOCATION_RATE,
                0,
                1024 * 502, 1024 * 716, 0,
                AbstractGCEvent.Generation.ALL,
                true);

        AbstractGCEvent<?> systemGcEvent = model.get(4);
        UnittestHelper.testMemoryPauseEvent(systemGcEvent,
                "System.gc() heap",
                AbstractGCEvent.Type.UJL_ZGC_GARBAGE_COLLECTION_SYSTEM_GC,
                0,
                1024 * 10124, 1024 * 5020, 0,
                AbstractGCEvent.Generation.ALL,
                true);
    }
}