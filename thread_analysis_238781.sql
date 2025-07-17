-- Thread Analysis for Slice ID 238781 (GAM_SDK_Initialization_Complete)
-- This slice ran on main thread: ample.gamsdkpoc [18515]

-- 1. Get complete details of slice 238781
SELECT 
    slice.id as slice_id,
    slice.name as trace_name,
    slice.ts as start_timestamp_ns,
    slice.ts / 1000000.0 as start_time_ms,
    slice.dur as duration_ns,
    slice.dur / 1000000.0 as duration_ms,
    thread.name as thread_name,
    thread.tid as thread_id,
    process.name as process_name,
    process.pid as process_id,
    CASE 
        WHEN thread.name = 'com.example.gamsdkpoc' THEN 'MAIN THREAD'
        WHEN thread.name LIKE '%Thread-%' THEN 'BACKGROUND THREAD'
        ELSE 'OTHER THREAD'
    END as thread_type
FROM slice 
JOIN thread_track ON slice.track_id = thread_track.id
JOIN thread ON thread_track.utid = thread.utid
JOIN process ON thread.upid = process.upid
WHERE slice.id = 238781;

-- 2. Find all GAM SDK related traces and their threads
SELECT 
    slice.id as slice_id,
    slice.name as trace_name,
    slice.ts / 1000000.0 as start_time_ms,
    slice.dur / 1000000.0 as duration_ms,
    thread.name as thread_name,
    thread.tid as thread_id,
    CASE 
        WHEN thread.name = 'com.example.gamsdkpoc' THEN 'MAIN THREAD'
        WHEN thread.name LIKE '%Thread-%' THEN 'BACKGROUND THREAD'
        ELSE 'OTHER THREAD'
    END as thread_type,
    CASE 
        WHEN thread.name = 'com.example.gamsdkpoc' THEN '❌ BLOCKING MAIN'
        ELSE '✅ NON-BLOCKING'
    END as performance_impact
FROM slice 
JOIN thread_track ON slice.track_id = thread_track.id
JOIN thread ON thread_track.utid = thread.utid
JOIN process ON thread.upid = process.upid
WHERE process.name = 'com.example.gamsdkpoc'
  AND slice.name LIKE 'GAM_SDK_%'
ORDER BY slice.ts;

-- 3. Thread switching analysis - Find when GAM SDK operations switched threads
WITH gam_traces AS (
    SELECT 
        slice.id,
        slice.name,
        slice.ts,
        slice.dur,
        thread.name as thread_name,
        thread.tid,
        ROW_NUMBER() OVER (ORDER BY slice.ts) as sequence_num
    FROM slice 
    JOIN thread_track ON slice.track_id = thread_track.id
    JOIN thread ON thread_track.utid = thread.utid
    JOIN process ON thread.upid = process.upid
    WHERE process.name = 'com.example.gamsdkpoc'
      AND slice.name LIKE 'GAM_SDK_%'
)
SELECT 
    curr.id as slice_id,
    curr.name as trace_name,
    curr.ts / 1000000.0 as start_time_ms,
    curr.dur / 1000000.0 as duration_ms,
    curr.thread_name,
    curr.tid,
    prev.thread_name as previous_thread,
    prev.tid as previous_tid,
    CASE 
        WHEN curr.thread_name != prev.thread_name THEN '🔄 THREAD SWITCH'
        ELSE '➡️ SAME THREAD'
    END as thread_change,
    CASE 
        WHEN curr.thread_name = 'com.example.gamsdkpoc' AND prev.thread_name != 'com.example.gamsdkpoc' 
        THEN '⚠️ SWITCHED TO MAIN THREAD'
        WHEN curr.thread_name != 'com.example.gamsdkpoc' AND prev.thread_name = 'com.example.gamsdkpoc' 
        THEN '✅ SWITCHED TO BACKGROUND'
        ELSE '➡️ NO CRITICAL SWITCH'
    END as critical_analysis
FROM gam_traces curr
LEFT JOIN gam_traces prev ON curr.sequence_num = prev.sequence_num + 1
ORDER BY curr.ts;

-- 4. Timeline analysis - When was slice 238781 active on main thread
SELECT 
    '238781 Timeline Analysis' as analysis_type,
    slice.ts / 1000000.0 as start_time_ms,
    (slice.ts + slice.dur) / 1000000.0 as end_time_ms,
    slice.dur / 1000000.0 as duration_ms,
    slice.ts as start_timestamp_ns,
    slice.ts + slice.dur as end_timestamp_ns,
    thread.name as thread_name,
    'Main thread was blocked for ' || (slice.dur / 1000000.0) || 'ms' as impact_summary
FROM slice 
JOIN thread_track ON slice.track_id = thread_track.id
JOIN thread ON thread_track.utid = thread.utid
WHERE slice.id = 238781;

-- 5. Context analysis - What was happening around slice 238781
WITH context_window AS (
    SELECT 
        slice.id,
        slice.name,
        slice.ts,
        slice.dur,
        thread.name as thread_name,
        thread.tid
    FROM slice 
    JOIN thread_track ON slice.track_id = thread_track.id
    JOIN thread ON thread_track.utid = thread.utid
    JOIN process ON thread.upid = process.upid
    WHERE process.name = 'com.example.gamsdkpoc'
      AND slice.ts BETWEEN 
          (SELECT slice.ts - 10000000 FROM slice WHERE id = 238781) AND  -- 10ms before
          (SELECT slice.ts + slice.dur + 10000000 FROM slice WHERE id = 238781)  -- 10ms after
)
SELECT 
    id as slice_id,
    name as trace_name,
    ts / 1000000.0 as start_time_ms,
    dur / 1000000.0 as duration_ms,
    thread_name,
    tid,
    CASE 
        WHEN id = 238781 THEN '🎯 TARGET SLICE'
        WHEN thread_name = 'com.example.gamsdkpoc' THEN '⚠️ MAIN THREAD'
        ELSE '✅ BACKGROUND'
    END as thread_analysis,
    CASE 
        WHEN ts < (SELECT ts FROM slice WHERE id = 238781) THEN 'BEFORE'
        WHEN ts > (SELECT ts + dur FROM slice WHERE id = 238781) THEN 'AFTER'
        ELSE 'CONCURRENT'
    END as timing_relation
FROM context_window
ORDER BY ts;

-- 6. Main thread blocking analysis
SELECT 
    'Main Thread Blocking Summary' as summary_type,
    COUNT(*) as total_gam_traces,
    COUNT(CASE WHEN thread.name = 'com.example.gamsdkpoc' THEN 1 END) as main_thread_traces,
    COUNT(CASE WHEN thread.name != 'com.example.gamsdkpoc' THEN 1 END) as background_traces,
    SUM(CASE WHEN thread.name = 'com.example.gamsdkpoc' THEN slice.dur ELSE 0 END) / 1000000.0 as total_main_thread_blocking_ms,
    AVG(CASE WHEN thread.name = 'com.example.gamsdkpoc' THEN slice.dur ELSE NULL END) / 1000000.0 as avg_main_thread_duration_ms,
    MAX(CASE WHEN thread.name = 'com.example.gamsdkpoc' THEN slice.dur ELSE 0 END) / 1000000.0 as max_main_thread_duration_ms
FROM slice 
JOIN thread_track ON slice.track_id = thread_track.id
JOIN thread ON thread_track.utid = thread.utid
JOIN process ON thread.upid = process.upid
WHERE process.name = 'com.example.gamsdkpoc'
  AND slice.name LIKE 'GAM_SDK_%';

-- 7. Specific query for slice 238781 thread switch detection
WITH target_slice AS (
    SELECT ts, dur FROM slice WHERE id = 238781
),
related_gam_traces AS (
    SELECT 
        slice.id,
        slice.name,
        slice.ts,
        slice.dur,
        thread.name as thread_name,
        thread.tid,
        CASE 
            WHEN slice.ts < (SELECT ts FROM target_slice) THEN 'BEFORE_238781'
            WHEN slice.ts >= (SELECT ts FROM target_slice) AND 
                 slice.ts < (SELECT ts + dur FROM target_slice) THEN 'DURING_238781'
            ELSE 'AFTER_238781'
        END as timing_phase
    FROM slice 
    JOIN thread_track ON slice.track_id = thread_track.id
    JOIN thread ON thread_track.utid = thread.utid
    JOIN process ON thread.upid = process.upid
    WHERE process.name = 'com.example.gamsdkpoc'
      AND slice.name LIKE 'GAM_SDK_%'
      AND slice.ts BETWEEN 
          (SELECT ts - 50000000 FROM target_slice) AND  -- 50ms before
          (SELECT ts + dur + 50000000 FROM target_slice)  -- 50ms after
)
SELECT 
    id as slice_id,
    name as trace_name,
    ts / 1000000.0 as start_time_ms,
    dur / 1000000.0 as duration_ms,
    thread_name,
    tid,
    timing_phase,
    CASE 
        WHEN thread_name = 'com.example.gamsdkpoc' THEN '❌ MAIN THREAD EXECUTION'
        ELSE '✅ BACKGROUND EXECUTION'
    END as execution_analysis
FROM related_gam_traces
ORDER BY ts;
