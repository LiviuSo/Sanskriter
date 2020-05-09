package com.android.lvicto.zombie.coroutines

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.lvicto.zombie.R
import com.android.lvicto.zombie.coroutines.retailxsimu.StlResultsActivity
import com.android.lvicto.zombie.keyboard.ims.view.keyboard.CustomKeyboardView
import kotlinx.android.synthetic.main.activity_coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.*
import kotlin.system.measureTimeMillis

class CoroutinesActivity : AppCompatActivity() {

    companion object {
        const val LOG_COR = "coroutines_learn"
        const val LOG_INLINE = "inlines"
    }

    @ExperimentalStdlibApi
    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @ObsoleteCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutines)

        // region retailx simu
        // retailx (flows) simu with coroutines
        // may develop into a stand-alone app (or be integrated in retailx)
        buttonCoroutineAdHoc.setOnClickListener {
            log("Retailx simu")
            startActivity(Intent(this, StlResultsActivity::class.java))
        }
        // endregion

        // region Baeldung - intro to kotlin coroutines
        // https://www.baeldung.com/kotlin-coroutines
        val fibonacciSeq = sequence {
            var a = 0
            var b = 1

            yield(1)

            while (true) {
                yield(a + b)

                val tmp = a + b
                a = b
                b = tmp
            }
        }

        suspend fun longComp(res: MutableList<String>) {
            log("longComp - before delay()")
            delay(1200L)
            log("longComp - before log")
            res.add(", world!")
        }

        suspend fun longComp(delay: Long) {
            delay(delay)
        }

        buttonBaeldungIntroKCoroutines.setOnClickListener {
            // sequence
//            val res = fibonacciSeq
//                    .take(5)
//                    .toList()
//            repeat(res.size) {
//                log("${res[it]}")
//            }

            // launch
//            val res2 = mutableListOf<String>()
//            runBlocking {
//                val promise = launch {
//                    longComp(res2)
//                }
//                res2.add("Hello")
//                promise.join()
//            }
//            repeat(res2.size) {
//                log(res2[it])
//            }

            // coroutine are light-weighted
//            runBlocking<Unit> {
//                // given
//                val counter = AtomicInteger(0)
//                val numberOfCoroutines = 100_000
//
//                // when
//                val jobs = List(numberOfCoroutines) {
//                    launch(Dispatchers.Default) {
//                        delay(1000L)
//                        counter.incrementAndGet()
//                    }
//                }
//                jobs.forEach { it.join() }
//
//                // then
//                log("${counter.get()}")

            // cancellation
//            runBlocking<Unit> {
//                // given
//                val job = launch(Dispatchers.Default) {
//                    while (isActive) {
//                        log("is working")
//                    }
//                }
//                delay(1300L)
//                // when
//                job.cancel()
//                // then cancel successfully
//                log("cancelled")
//            }

            // timeout
//            runBlocking {
//                try {
//                    withTimeout(1300L) {
//                        repeat(1000) { i ->
//                            log("Some expensive computation $i ...")
//                            delay(500L)
//                        }
//                    }
//                } catch (e: TimeoutCancellationException) {
//                    log("timeout-ed")
//                }
//            }

            // asynch
            runBlocking<Unit> {
                val delay = 1000L
                val time = measureTimeMillis {
                    // given
                    val one = async(Dispatchers.Default) {
                        longComp(delay)
                    }
                    val two = async(Dispatchers.Default) {
                        longComp(delay)
                    }

                    // when
                    runBlocking {
                        one.await()
                        two.await()
                    }
                }

                // then
                log("$time < $delay * 2")
            }

            // asynch LAZY
            runBlocking<Unit> {
                val delay = 1000L
                val time = measureTimeMillis {
                    // given
                    val one = async(Dispatchers.Default, CoroutineStart.LAZY) {
                        longComp(delay)
                    }
                    val two = async(Dispatchers.Default, CoroutineStart.LAZY) {
                        longComp(delay)
                    }

                    // when
                    runBlocking {
                        one.await()
                        two.await()
                    }
                }

                // then
                log("$time < $delay * 2")
            }

            // end
            log("done!")
        }

        // endregion

        // region example inline, noinline, crossinline
        val res = higherOrderFunction({
            true
        }, {
            2
        })
        Log.d(LOG_INLINE, "higherOrderFunction() -> $res")

        // crossinline
        higherOrderFunction2 {
//            return // not allowed
        }
        // endregion

        // region "Basics"
        buttonBasics.setOnClickListener {
//            main1()
//            main2()
//            runBlocking {
//                coroutineScope {
//                    main3()
//                }
//            }
//            main4()
            main5()
//            main6()
        }
        // endregion

        // region "Cancellation and Timeouts"
        buttonCancelTimeout.setOnClickListener {
            main7()
            main8()
            main9()
            main10()
            main11()
            main12()
            main13()
            main14()
        }
        // endregion

        // region detect long tap with coroutines
        buttonLongTapDetect.setOnTouchListener(object : BaseTouchListener2() {
            override fun doOnLongTap() {
                Log.d(LOG_COR, "doOnLongTap()")
            }

            override fun doOnNormalTapOnly() {
                Log.d(LOG_COR, "doOnNormalTapOnly()")
            }

            override fun doOnActionDown() {
                Log.d(LOG_COR, "doOnActionDown()")
            }
        })
        // endregion

        // region "Composing suspending functions"
        buttonComposingSuspendables.setOnClickListener {
            main14()
            main15()
            main16()
            main17()
            main18()
            main19()
            main20()
        }
        // endregion

        // region "Coroutine Context and Dispatchers"
        buttonCoroutineContetAndDispatchers.setOnClickListener {
//            main21()
//            main22()
//            main23()
//            main24()
            main25()
//            main26()
//            main27()
//            main28()
//            main29()
        }
        // endregion

        // region "Asynchronous Flow"
        buttonAsynchFlow.setOnClickListener {
//            main30()
//            main31()
//            main32()
            main32a()
        }
        // endregion

        // region "Channels"
        buttonChannels.setOnClickListener {
            main33()
        }
        // endregion

        // region "Exception Handling"
        buttonExceptionHandling.setOnClickListener {
            main33()
        }
        // endregion

        // region "Shared mutable state and concurrency"
        buttonSharedMutableState.setOnClickListener {
//            main34()
//            main35()
//            main36()
//            main37()
//            main38()
//            main39()
//            main40()
            main41()
        }
        // endregion

        // region "Select Expression"
        buttonSelectExpression.setOnClickListener {
//            main42()
            main43()
        }
        // endregion
    }
///////////////////////////////////////////////////////////////////////////////////////////////

    // region "Select Expression"
    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    fun main43() = runBlocking<Unit> {
        val a = produce<String> {
            repeat(4) {
                delay(10)
                send("Hello $it")
            }
        }
        val b = produce<String> {
            repeat(4) { send("World $it") }
        }
        repeat(8) { // print first eight results
            log(selectAorB(a, b))
        }
        coroutineContext.cancelChildren()
    }

    @ObsoleteCoroutinesApi
    suspend fun selectAorB(a: ReceiveChannel<String>, b: ReceiveChannel<String>): String =
            select<String> {
                a.onReceiveOrNull { value ->
                    if (value == null)
                        "Channel 'a' is closed"
                    else
                        "a -> '$value'"
                }
                b.onReceiveOrNull { value ->
                    if (value == null)
                        "Channel 'b' is closed"
                    else
                        "b -> '$value'"
                }
            }

    @ExperimentalCoroutinesApi
    private fun main42() = runBlocking {
        val fizz = fizz()
        val buzz = buzz()
        repeat(7) {
            selectFizzBuzz(fizz, buzz)
        }
        coroutineContext.cancelChildren() // cancel fizz & buzz coroutines
    }

    private suspend fun selectFizzBuzz(fizz: ReceiveChannel<String>, buzz: ReceiveChannel<String>) {
        select<Unit> { // <Unit> means that this select expression does not produce any result
            fizz.onReceive { value ->  // this is the first select clause
                log("fizz -> '$value'")
            }
            buzz.onReceive { value ->  // this is the second select clause
                log("buzz -> '$value'")
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun CoroutineScope.fizz() = produce<String> {
        while (true) { // sends "Fizz" every 300 ms
            delay(300)
            send("Fizz")
        }
    }

    @ExperimentalCoroutinesApi
    fun CoroutineScope.buzz() = produce<String> {
        while (true) { // sends "Buzz!" every 500 ms
            delay(500)
            send("Buzz!")
        }
    }
// endregion

// region "Shared mutable state and concurrency"

    var continuation: Continuation<String>? = null

    private fun main41() {
        val job = GlobalScope.launch(Dispatchers.Unconfined) {
            while (true) {
                log("suspending...")
                log(suspendHere())
                log("resumed")
            }
        }
        continuation!!.resume("Resuming first time")
        continuation!!.resume("Resuming second time")
        continuation!!.resume("Resuming third time")
    }

    suspend fun suspendHere() = suspendCancellableCoroutine<String> {
        log("suspended")
        continuation = it
    }

    // Message types for counterActor
    open class CounterMsg
    object IncCounter : CounterMsg() // one-way message to increment counter
    class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // a request with reply

    // This function launches a new counter actor
    @ObsoleteCoroutinesApi
    fun CoroutineScope.counterActor() = actor<CounterMsg> {
        var counter = 0 // actor state
        for (msg in channel) { // iterate over incoming messages
            when (msg) {
                is IncCounter -> counter++
                is GetCounter -> msg.response.complete(counter)
            }
        }
    }

    fun main40() = runBlocking<Unit> {
        val counter = counterActor() // create the actor
        withContext(Dispatchers.Default) {
            massiveRun {
                counter.send(IncCounter)
            }
        }
        // send a message to get a counter value from an actor
        val response = CompletableDeferred<Int>()
        counter.send(GetCounter(response))
        println("Counter = ${response.await()}")
        counter.close() // shutdown the actor
    }

    fun main39() = runBlocking {
        val mutex = Mutex()
        var counter = 0
        withContext(Dispatchers.Default) {
            massiveRun {
                // protect each increment with lock
                mutex.withLock {
                    counter++
                }
            }
        }
        println("Counter = $counter")
    }

    @ObsoleteCoroutinesApi
    fun main38() = runBlocking {
        val counterContext = newSingleThreadContext("CounterContext")
        var counter = 0
        // confine everything to a single-threaded context
        withContext(counterContext) {
            massiveRun {
                counter++
            }
        }
        println("Counter = $counter")
    }

    // Thread confinement
    @ObsoleteCoroutinesApi
    fun main37() = runBlocking {
        val counterContext = newSingleThreadContext("CounterContext")
        var counter = 0
        withContext(Dispatchers.Default) {
            massiveRun {
                // confine each increment to a single-threaded context
                withContext(counterContext) {
                    counter++
                }
            }
        }
        log("Counter = $counter")
    }

    fun main36() = runBlocking {
        val counter = AtomicInteger()
        withContext(Dispatchers.Default) {
            massiveRun {
                counter.incrementAndGet()
            }
        }
        log("Counter = $counter")
    }

    private fun main35() = runBlocking {
        var counter = 0
        withContext(Dispatchers.Default) {
            massiveRun {
                counter++
            }
        }
        log("Counter = $counter")
    }

    private suspend fun massiveRun(action: suspend () -> Unit) {
        val n = 100  // number of coroutines to launch
        val k = 1000 // times an action is repeated by each coroutine
        val time = measureTimeMillis {
            coroutineScope { // scope for coroutines
                repeat(n) {
                    launch {
                        repeat(k) { action() }
                    }
                }
            }
        }
        log("Completed ${n * k} actions in $time ms")
    }

    private fun main34() {
        runBlocking {
            withContext(Dispatchers.Default + CoroutineName("simultaneous")) {
                async {
                    for (i in 0..20) {
                        log("processing item: $i")
                    }
                }.start()
                async {
                    for (i in 0..30) {
                        log("processing item: $i")
                        delay(80)
                    }
                }.start()
            }
        }
    }
// endregion

// region "Exception Handling"
// endregion

    // region "Channels"
    private fun main33() = runBlocking {
        withContext(Dispatchers.IO) {
            val ret = if (someValue() > 40) {
                doSomethingUsefulOne()
            } else {
                doSomethingUsefulTwo()
            }
            log("main33(): val = $ret")
        }
    }

    private suspend fun someValue(): Int {
        log("start someValue()")
        delay(200)
        log("end someValue()")
        return 44
    }

// endregion

// region "Asynchronous Flow"

    data class VicsKey(val name: String) : AbstractCoroutineContextElement(VicsKey) {
        companion object Key : CoroutineContext.Key<VicsKey>

    }

    private fun main32a() {
        runBlocking(VicsKey("test")) {
            log(coroutineContext[VicsKey]?.name ?: "no VicsKey")
            coroutineContext[Job]?.cancel("Prematurely")
            log("main thread start")
            GlobalScope.launch {
                log("clear DB (start)")
                delay(200)
                log("clear DB (end)")
            }
            launch(Dispatchers.IO) {
                log("fetch from DB (start)")
                delay(300)
                log("fetch from DB (end)")
            }
            log("main thread end")
        }
    }

    private val collector = object : FlowCollector<Int> {
        override suspend fun emit(value: Int) {
            delay(100)
            log("value : $value")
        }
    }
    private val collectorString = object : FlowCollector<String> {
        override suspend fun emit(value: String) {
            delay(95)
            log("value : $value")
        }
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    private fun main32() {
        runBlocking {
            val res = flowOf("En", "Fr", "Ro", "Sa")
                    .map {
                        it.toUpperCase(Locale.getDefault())
                    }
                    .reduce { s1, s2 ->
                        "$s1 + $s2"
                    }
            log(res)
        }
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    private fun main31() {
        runBlocking {
            flowOf("En", "Fr", "Ro", "Sa")
                    .map {
                        it.toUpperCase(Locale.getDefault())
                    }
                    .take(2)
                    .collect(collector = collectorString)
        }
    }

    private fun foo(): Flow<Int> = flow {
        log("Flow started")
        for (i in 1..3) {
            delay(100)
            emit(i)
        }
    }

    @InternalCoroutinesApi
    private fun main30() = runBlocking {
        log("Calling foo...")

        val flow = foo()
        log("Calling collect...")
        flow.collect(collector)
        log("Calling collect again...")
        flow.collect(collector)
    }
// endregion

    // region "Coroutine Context and Dispatchers"
    private val threadLocal = ThreadLocal<String?>() // declare thread-local variable

    private fun main29() = runBlocking<Unit> {
        threadLocal.set("main")
        log("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
            log("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
            yield()
            log("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        }
        job.join()
        log("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    }

    private fun main28() = runBlocking {
        val mainCS = MainScope()

        mainCS.launch(Dispatchers.Default) {
            log("cor1 in mainScope start")
            delay(1100)
            log("cor1 in mainScope end")
        }

        mainCS.launch(Dispatchers.Unconfined) {
            log("cor2 in mainScope start")
            delay(100)
            log("cor2 in mainScope end")
        }

        log("mainScope after launching cors")
        mainCS.cancel()
        log("mainScope after cancel")

    }


    // not working: doesn't print the coroutine name
    private fun main27() = runBlocking(CoroutineName("main")) {
        log("Started main coroutine")
        // run two background value computations
        val v1 = async(CoroutineName("v1coroutine")) {
            delay(500)
            log("Computing v1")
            252
        }
        val v2 = async(CoroutineName("v2coroutine")) {
            delay(1000)
            log("Computing v2")
            6
        }
        log("The answer for v1 / v2 = ${v1.await() / v2.await()}")
    }

    private fun main26() = runBlocking<Unit> {
        // launch a coroutine to process some kind of incoming request
        val request = launch {
            repeat(3) { i -> // launch a few children jobs
                launch {
                    delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
                    log("Coroutine $i is done")
                }
            }
            log("request: I'm done and I don't explicitly join my children that are still active")
        }
        request.join() // wait for completion of the request, including all its children
        log("Now processing of the request is complete")
    }

    private fun main25() = runBlocking<Unit> {
        // launch a coroutine to process some kind of incoming request
        val request = launch {
            // it spawns two other jobs, one with GlobalScope
            GlobalScope.launch {
                log("job1: I run in GlobalScope and execute independently!")
                delay(1000)
                log("job1: I am not affected by cancellation of the request")
            }
            // and the other inherits the parent context
            launch {
                delay(100)
                log("job2: I am a child of the request coroutine")
                delay(1000)
                log("job2: I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500)
        request.cancel() // cancel processing of the request
        delay(1000) // delay a second to see what happens
        log("main: Who has survived request cancellation?")
    }

    @ObsoleteCoroutinesApi
    private fun main24() {
        newSingleThreadContext("Ctx1").use { ctx1 ->
            newSingleThreadContext("Ctx2").use { ctx2 ->
                runBlocking(ctx1) {
                    log("Started in ctx1")
                    withContext(ctx2) {
                        log("Working in ctx2")
                    }
                    log("Back to ctx1")
                }
            }
        }
    }

    fun log(msg: String) = Log.d(LOG_COR, "[${Thread.currentThread().name}] $msg")

    private fun main23() = runBlocking<Unit> {
        val a = async {
            log("I'm computing a piece of the answer")
            6
        }
        val b = async {
            log("I'm computing another piece of the answer")
            7
        }
        log("The answer is ${a.await() * b.await()}")
    }

    private fun main22() = runBlocking<Unit> {
        Log.d(LOG_COR, "main22() start     : I'm working in thread ${Thread.currentThread().name}")
        launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
            Log.d(LOG_COR, "Unconfined      : I'm working in thread ${Thread.currentThread().name}")
            delay(500)
            Log.d(LOG_COR, "Unconfined      : After delay 500 in thread ${Thread.currentThread().name}")
        }
        Log.d(LOG_COR, "main22() middle     : I'm working in thread ${Thread.currentThread().name}")
        launch { // context of the parent, main runBlocking coroutine
            Log.d(LOG_COR, "main runBlocking: I'm working in thread ${Thread.currentThread().name}")
            delay(1000)
            Log.d(LOG_COR, "main runBlocking: After delay 1000 in thread ${Thread.currentThread().name}")
        }
        Log.d(LOG_COR, "main22() after     : I'm working in thread ${Thread.currentThread().name}")
    }

    @ObsoleteCoroutinesApi
    private fun main21() = runBlocking<Unit> {
        launch { // context of the parent, main runBlocking coroutine
            Log.d(LOG_COR, "main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
            Log.d(LOG_COR, "Unconfined            : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Default) { // will get dispatched to DefaultDispatcher
            Log.d(LOG_COR, "Default               : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
            Log.d(LOG_COR, "newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
        }
    }
// endregion

    // region "Composing suspending functions"
    private fun main20() = runBlocking<Unit> {
        try {
            failedConcurrentSum()
        } catch (e: ArithmeticException) {
            Log.d(LOG_COR, "Computation failed with ArithmeticException")
        }
    }

    private suspend fun failedConcurrentSum(): Int = coroutineScope {
        val one = async<Int> {
            try {
                delay(Long.MAX_VALUE) // Emulates very long computation
                42
            } finally {
                Log.d(Companion.LOG_COR, "First child was cancelled")
            }
        }
        val two = async<Int> {
            delay(1000L)
            Log.d(Companion.LOG_COR, "Second child throws an exception")
            throw ArithmeticException()
        }
        one.await() + two.await()
    }

    private fun main19() {
        runBlocking {
            val time = measureTimeMillis {
                Log.d(Companion.LOG_COR, "The answer is ${concurrentSum()}")
            }
            Log.d(Companion.LOG_COR, "Completed in $time ms")
        }
    }

    private suspend fun concurrentSum(): Int = coroutineScope {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        one.await() + two.await()
    }

    // note that we don't have `runBlocking` to the right of `main` in this example
    private fun main18() {
        val time = measureTimeMillis {
            // we can initiate async actions outside of a coroutine
            val one = somethingUsefulOneAsync()
            val two = somethingUsefulTwoAsync()
            // but waiting for a result must involve either suspending or blocking.
            // here we use `runBlocking { ... }` to block the main thread while waiting for the result
            runBlocking {
                Log.d(Companion.LOG_COR, "The answer is ${one.await() + two.await()}")
            }
        }
        Log.d(Companion.LOG_COR, "Completed in $time ms")
    }

    // The result type of somethingUsefulOneAsync is Deferred<Int>
    private fun somethingUsefulOneAsync() = GlobalScope.async {
        doSomethingUsefulOne()
    }

    // The result type of somethingUsefulTwoAsync is Deferred<Int>
    private fun somethingUsefulTwoAsync() = GlobalScope.async {
        doSomethingUsefulTwo()
    }

    private fun main17() {
        val time = measureTimeMillis {
            runBlocking {
                val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
                val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
                one.start()
                two.start()
                Log.d(LOG_COR, "Total = ${one.await() + two.await()}")
            }
        }
        Log.d(LOG_COR, "Time = $time")
    }

    private fun main16() {
        val time = measureTimeMillis {
            runBlocking {
                val one = async { doSomethingUsefulOne() }
                val two = async { doSomethingUsefulTwo() }
                Log.d(LOG_COR, "Total = ${one.await() + two.await()}")
            }
        }
        Log.d(LOG_COR, "Time = $time")
    }

    private fun main15() {
        val time = measureTimeMillis {
            runBlocking {
                val one = doSomethingUsefulOne()
                val two = doSomethingUsefulTwo()
                Log.d(LOG_COR, "Total = ${one + two}")
            }
        }
        Log.d(LOG_COR, "Time = $time")
    }

    private suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // pretend we are doing something useful here
        return 13
    }

    private suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // pretend we are doing something useful here, too
        return 29
    }
// endregion

    // region "Cancellation and Timeouts"
    private fun main14() = runBlocking {
        var isLongTap = false
        val job = launch {
            Log.d(LOG_COR, "Waiting for long tap...")
            delay(1500)
            Log.d(LOG_COR, "Long tap detected.")
            isLongTap = true
        }
        Log.d(LOG_COR, "Waiting to release the button...")
        delay(1900)
        if (job.isActive) {
            Log.d(LOG_COR, "Button released.")
            job.cancel()
            isLongTap = false
        }
        Log.d(LOG_COR, "isLongTap = $isLongTap")
    }

    private fun main13() = runBlocking {
        val result = withTimeoutOrNull(1300L) {
            repeat(1000) { i ->
                Log.d(LOG_COR, "I'm sleeping $i ...")
                delay(500L)
            }
            "Done" // will get cancelled before it produces this result
        }
        Log.d(LOG_COR, "Result is $result")
    }

    private fun main12() = runBlocking {
        withTimeout(1300L) {
            repeat(1000) { i ->
                Log.d(LOG_COR, "I'm sleeping $i ...")
                delay(500L)
            }
        }
    }

    private fun main11() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    Log.d(LOG_COR, "job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                withContext(NonCancellable) {
                    Log.d(LOG_COR, "job: I'm running finally")
                    delay(1000L)
                    Log.d(LOG_COR, "job: And I've just delayed for 1 sec because I'm non-cancellable")
                }
            }
        }
        delay(1300L) // delay a bit
        Log.d(LOG_COR, "main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        Log.d(LOG_COR, "main: Now I can quit.")
    }

    private fun main10() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                Log.d(LOG_COR, "job: I'm running finally")
            }
        }
        delay(1300L) // delay a bit
        Log.d(LOG_COR, "main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        Log.d(LOG_COR, "main: Now I can quit.")
    }

    private fun main9() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (isActive) { // cancellable computation loop
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    Log.d(LOG_COR, "job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        Log.d(LOG_COR, "main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        Log.d(LOG_COR, "main: Now I can quit.")
    }

    private fun main8() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) { // computation loop, just wastes CPU
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    Log.d(LOG_COR, "job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        Log.d(LOG_COR, "main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        Log.d(LOG_COR, "main: Now I can quit.")
    }

    private fun main7() = runBlocking {
        val job = launch {
            repeat(1000) { i ->
                Log.d(LOG_COR, "job: I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L) // delay a bit
        Log.d(LOG_COR, "main: I'm tired of waiting!")
        job.cancel() // cancels the job
        job.join() // waits for job's completion
        Log.d(LOG_COR, "main: Now I can quit.")
    }
// endregion

// region "Basics"

    private fun main6() = runBlocking {
        Log.d(LOG_COR, "main6() Before launch { doWorld() }")
        launch(Dispatchers.Default) { doWorld() }
        Log.d(LOG_COR, "main6() Hello,")
    }

    // this is your first suspending function
    private suspend fun doWorld() {
        Log.d(LOG_COR, "doWorld() launched; before delay") // print after delay
        delay(1000L)
        Log.d(LOG_COR, "doWorld() World") // print after delay
    }

    private fun main5() = runBlocking { // this: CoroutineScope
        launch {
//            delay(200L)
            log("main5() Task from runBlocking") // print after delay
        }

        coroutineScope { // Creates a coroutine scope
            launch {
                withContext(Dispatchers.IO) {
                    delay(200L)
                    log("main5() Task from nested launch withContext()") // print after delay
                }
                async {
                    log("main5() Task from nested launch async()") // print after delay
                }

//                delay(500L)
                log("main5() Task from nested launch") // print after delay
            }

//            delay(100L)
            log("main5() Task from coroutine scope") // print after delay
        }

        Log.d(LOG_COR, "main5() Coroutine scope is over") // print after delay
    }

    private fun main4() = runBlocking { // this: CoroutineScope
        launch { // launch a new coroutine in the scope of runBlocking
            delay(1000L)
            Log.d(LOG_COR, "main4() World") // print after delay
        }
        Log.d(LOG_COR, "main4() Hello,") // main thread continues while coroutine is delayed
    }

    private suspend fun main3() {
        val job = GlobalScope.launch { // launch a new coroutine and keep a reference to its Job
            delay(1000L)
            Log.d(LOG_COR, "main3() World") // print after delay
        }
        Log.d(LOG_COR, "main3() Hello,") // main thread continues while coroutine is delayed
        job.join() // wait until child coroutine completes
    }

    private fun main2() {
        GlobalScope.launch { // launch a new coroutine in background and continue
            delay(1000L)
            Log.d(LOG_COR, "main2() World") // print after delay
        }
        Log.d(LOG_COR, "main2() Hello,") // main thread continues while coroutine is delayed
        runBlocking {     // but this expression blocks the main thread
            delay(2000L)  // ... while we delay for 2 seconds to keep JVM alive
        }
    }

    // first coroutine
    private fun main1() {
        GlobalScope.launch { // launch a new coroutine in background and continue
            delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
            Log.d(LOG_COR, "main1() World") // print after delay
        }
        Log.d(LOG_COR, "main1() Hello,") // main thread continues while coroutine is delayed
        Thread.sleep(2000L) // block main thread for 2 seconds to keep JVM alive
    }

// endregion

    // region inline, noinline, crossinline
    private inline fun higherOrderFunction(lambda: () -> Boolean, crossinline lambda2: () -> Int): Int {
        if (lambda()) {
            return lambda2()
        }
        return 1
    }


    inline fun higherOrderFunction2(crossinline lambda: () -> Unit) {
        normalFunction {
            lambda()
        }
    }

    inline fun higherOrderFunction3(lambda: () -> Unit) {
        normalFunction {
//            lambda() // needs crossinline
        }
    }

    fun normalFunction(lambda: () -> Unit) {
        // do smth
        return
    }
// endregion

    abstract class BaseTouchListener2 : View.OnTouchListener {

        //        protected lateinit var keyView: BaseKeyView
        protected lateinit var keyboardView: CustomKeyboardView

        private var longTap = AtomicBoolean(false)
        private lateinit var job: Job

        override fun onTouch(view: View, event: MotionEvent): Boolean = when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // fetch the key view
//                keyView = view as BaseKeyView
                // inject keyboard view here
//                Injector.getInstance(keyView.context).let {
//                    keyboardView = it.ims.keyboardView
//                }
                doOnActionDown()
                job = waitForLongTap() // start waiting for a long tap
                true
            }
            MotionEvent.ACTION_UP -> {
                stopWaitingForLongTap()
                if (!longTap.get()) {
                    doOnNormalTapOnly()
                }
                doOnActionUp()
                view.performClick()
                true
            }
            else -> {
                false
            }
        }

        private fun stopWaitingForLongTap() {
            runBlocking {
                if (job.isActive) {
                    job.cancel()
                    longTap.set(false)
                }
            }
        }

        private fun waitForLongTap() = GlobalScope.launch(Dispatchers.Default) {
            delay(LONG_PRESS_TIME) // waiting...
            longTap.set(true)
            doOnLongTap()
        }

        protected fun vibrate(long: Boolean = false) {
            // todo
        }

        @Override
        protected open fun doOnLongTap() {
        }

        @Override
        protected open fun doOnActionDown() {
        }

        @Override
        protected open fun doOnActionUp() {
//            keyView.setPressedUI(false)
        }

        @Override
        protected open fun doOnNormalTapOnly() {
        }

        companion object {
            const val DELAY_AUTO_REPEAT: Long = 130
            const val LONG_PRESS_TIME = 420L
            const val LOG = "BaseTouchListener"
        }
    }

}