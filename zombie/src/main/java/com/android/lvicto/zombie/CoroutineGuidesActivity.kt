package com.android.lvicto.zombie

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_coroutine_guides.*
import kotlinx.coroutines.*
import java.util.jar.Attributes
import kotlin.system.measureTimeMillis

class CoroutineGuidesActivity : AppCompatActivity() {

    companion object {
        private const val LOG_TAG = "Coroutines"

        fun println(string: String) {
            Log.d(LOG_TAG, string)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_guides)

        // samples "Coroutines Basics" (https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/basics.md)
        btnSample1.apply {
            text = "Structured concurrency"
            setOnClickListener {
                sample01Basics()
            }
        }
        btnSample2.apply {
            text = "Scope builder"
            setOnClickListener {
                sample02Basics()
            }
        }
        btnSample3.apply {
            text = "Extract function refactoring"
            setOnClickListener {
                sample03Basics()
            }
        }
        btnSample4.apply {
            text = "Coroutines ARE light-weight"
            setOnClickListener {
                sample04Basics()
            }
        }
        btnSample5.apply {
            text = "Global coroutines are like daemon threads"
            setOnClickListener {
                sample05Basics()
            }
        }

        // samples "Cancellations and timeouts" (https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/cancellation-and-timeouts.md)
        btnSample6.apply {
            text = "Cancelling coroutine execution"
            setOnClickListener {
                sample01CancelTimeout()
            }
        }

        btnSample7.apply {
            text = "Cancellation is cooperative"
            setOnClickListener {
                sample02CancelTimeout()
            }
        }

        btnSample8.apply {
            text = "Cancellation is cooperative [isActive]"
            setOnClickListener {
                sample03CancelTimeout()
            }
        }

        btnSample9.apply {
            text = "Close res with finally"
            setOnClickListener {
                sample04CancelTimeout()
            }
        }

        btnSample10.apply {
            text = "Run non-cancellable block"
            setOnClickListener {
                sample05CancelTimeout()
            }
        }

        btnSample11.apply {
            text = "Timeout"
            setOnClickListener {
                sample06CancelTimeout()
            }
        }

        // composing suspending functions (https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/composing-suspending-functions.md)
        btnSample12.apply {
            text = "Sequential by default"
            setOnClickListener {
                sample01Composing()
            }
        }
        btnSample13.apply {
            text = "Concurrent using async"
            setOnClickListener {
                sample02Composing()
            }
        }
        btnSample14.apply {
            text = "Lazily started async"
            setOnClickListener {
                sample03Composing()
            }
        }
        btnSample15.apply {
            text = "Async-style functions"
            setOnClickListener {
                sample04Composing()
            }
        }
        btnSample16.apply {
            text = "Structured concurrency with async"
            setOnClickListener {
                sample05Composing()
            }
        }
        btnSample17.apply {
            text = "Structured concurrency with async \n [cancel propagation]"
            setOnClickListener {
                sample06Composing()
            }
        }

        // Coroutine Context and Dispatchers (https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/coroutine-context-and-dispatchers.md#dispatchers-and-threads)
        btnSample18.apply {
            text = "Dispatchers and threads"
            setOnClickListener {
                sample01Dispatchers()
            }
        }
        btnSample19.apply {
            text = "Unconfined vs confined dispatcher"
            setOnClickListener {
                sample02Dispatchers()
            }
        }
        btnSample19.apply {
            text = "Debugging coroutines and threads"
            setOnClickListener {
                sample03Dispatchers()
            }
        }
        btnSample20.apply {
            text = "Jumping between threads"
            setOnClickListener {
                sample04Dispatchers()
            }
        }
        btnSample21.apply {
            text = "Children of a coroutine"
            setOnClickListener {
                sample05Dispatchers()
            }
        }
        btnSample22.apply {
            text = "Parental responsibilities"
            setOnClickListener {
                sample06Dispatchers()
            }
        }
        btnSample23.apply {
            text = "Naming coroutines for debugging"
            setOnClickListener {
                sample07Dispatchers()
            }
        }
        btnSample24.apply {
            text = "Combining context elements"
            setOnClickListener {
                sample08Dispatchers()
            }
        }
        btnSample25.apply {
            text = "Coroutine scope"
            setOnClickListener {
                sample09Dispatchers()
            }
        }
        btnSample26.apply {
            text = "Thread-local data"
            setOnClickListener {
                sample10Dispatchers()
            }
        }
    }

    private fun sample10Dispatchers() {
        val threadLocal = ThreadLocal<String?>() // declare thread-local variable

        runBlocking {
            threadLocal.set("main")
            println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
            val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
                println("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
                yield()
                println("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
            }
            job.join()
            println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        }
    }

    class Activity : CoroutineScope by CoroutineScope(Dispatchers.Default) {

        fun destroy() {
            cancel() // Extension on CoroutineScope
        }
        // to be continued ...

        // class Activity continues
        fun doSomething() {
            // launch ten coroutines for a demo, each working for a different time
            repeat(10) { i ->
                launch {
                    delay((i + 1) * 200L) // variable delay 200ms, 400ms, ... etc
                    println("Coroutine $i is done")
                }
            }
        }
    }

    private fun sample09Dispatchers() = runBlocking {
        val activity = Activity()
        activity.doSomething() // run test function
        println("Launched coroutines")
        delay(1500L) // delay for half a second
        println("Destroying activity!")
        activity.destroy() // cancels all coroutines
        delay(2000) // visually confirm that they don't work
    }

    private fun sample08Dispatchers() = runBlocking<Unit> {
        launch(Dispatchers.Default + CoroutineName("test")) {
            log("Me")
        }
    }

    private fun sample07Dispatchers() = runBlocking(CoroutineName("main")) {
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
        launch(CoroutineName("launch")) {
            delay(3100)
            log("I'm slower")
        }
    }

    private fun sample06Dispatchers() = runBlocking {
        // launch a coroutine to process some kind of incoming request
        val request = launch {
            repeat(3) { i ->
                // launch a few children jobs
                launch {
                    delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
                    println("Coroutine $i is done")
                }
            }
            println("request: I'm done and I don't explicitly join my children that are still active")
        }
        request.join() // wait for completion of the request, including all its children
        println("main: Now processing of the request is complete")
    }

    private fun sample05Dispatchers() = runBlocking<Unit> {
        // launch a coroutine to process some kind of incoming request
        val request = launch {
            // it spawns two other jobs, one with GlobalScope
            GlobalScope.launch {
                println("job1: I run in GlobalScope and execute independently!")
                delay(1000)
                println("job1: I am not affected by cancellation of the request")
            }
            // and the other inherits the parent context
            launch {
                delay(100)
                println("job2: I am a child of the request coroutine")
                delay(1000)
                println("job2: I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500)
        request.cancel() // cancel processing of the request
        delay(1000) // delay a second to see what happens
        println("main: Who has survived request cancellation?")
    }

    @UseExperimental(ObsoleteCoroutinesApi::class)
    private fun sample04Dispatchers() {
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

    private fun sample03Dispatchers() = runBlocking<Unit> {
        //sampleStart
        val a = async {
            log("I'm computing a piece of the answer")
            6
        }
        val b = async {
            log("I'm computing another piece of the answer")
            7
        }
        log("The answer is ${a.await() * b.await()}") // log ?
    }

    fun CoroutineScope.log(msg: String) = println("[thread:${Thread.currentThread().name} coroutine:${this.coroutineContext[CoroutineName]?.name}] $msg")

    private fun sample02Dispatchers() = runBlocking<Unit> {
        launch(Dispatchers.Unconfined) {
            // not confined -- will work with main thread
            println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
            delay(500)
            println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
        }
        launch {
            // context of the parent, main runBlocking coroutine
            println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
            delay(1000)
            println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
        }
    }

    @UseExperimental(ObsoleteCoroutinesApi::class)
    private fun sample01Dispatchers() = runBlocking {
        launch {
            // context of the parent, main runBlocking coroutine
            println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Unconfined) {
            // not confined -- will work with main thread
            println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Default) {
            // will get dispatched to DefaultDispatcher
            println("Default               : I'm working in thread ${Thread.currentThread().name}")
        }
        launch(newSingleThreadContext("MyOwnThread")) {
            // will get its own new thread
            println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    private fun sample06Composing() = runBlocking<Unit> {
        try {
            failedConcurrentSum()
        } catch (e: ArithmeticException) {
            println("Computation failed with ArithmeticException")
        }
    }

    suspend fun failedConcurrentSum(): Int = coroutineScope {
        val one = async {
            try {
                delay(Long.MAX_VALUE) // Emulates very long computation
                42
            } finally {
                println("First child was cancelled")
            }
        }
        val two = async<Int> {
            println("Second child throws an exception")
            throw ArithmeticException()
        }
        one.await() + two.await()
    }

    fun sample05Composing() = runBlocking<Unit> {
        val time = measureTimeMillis {
            println("The answer is ${concurrentSum()}")
        }
        println("Completed in $time ms")
    }

    suspend fun concurrentSum(): Int = coroutineScope {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        one.await() + two.await()
    }

    private fun sample04Composing() {
        val time = measureTimeMillis {
            // we can initiate async actions outside of a coroutine
            val one = somethingUsefulOneAsync()
            val two = somethingUsefulTwoAsync()
            // but waiting for a result must involve either suspending or blocking.
            // here we use `runBlocking { ... }` to block the main thread while waiting for the result
            runBlocking {
                println("The answer is ${one.await() + two.await()}")
            }
        }
        println("Completed in $time ms")
    }

    fun somethingUsefulOneAsync() = GlobalScope.async {
        doSomethingUsefulOne()
    }

    fun somethingUsefulTwoAsync() = GlobalScope.async {
        doSomethingUsefulTwo()
    }

    private fun sample03Composing() = runBlocking {
        val time = measureTimeMillis {
            val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
            val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
            // some computation
            one.start() // start the first one
            two.start() // start the second one
            println("The answer is ${one.await() + two.await()}")
        }
        println("Completed in $time ms")
    }

    private fun sample02Composing() = runBlocking {
        val time = measureTimeMillis {
            val one = async { doSomethingUsefulOne() }
            val two = async { doSomethingUsefulTwo() }
            println("The answer is ${one.await() + two.await()}")
        }
        println("Completed in $time ms")
    }

    private fun sample01Composing() = runBlocking {
        val time = measureTimeMillis {
            val one = doSomethingUsefulOne()
            val two = doSomethingUsefulTwo()
            println("The answer is ${one + two}")
        }
        println("Completed in time $time ms")
    }

    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // pretend we are doing something useful here
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // pretend we are doing something useful here, too
        return 29
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private fun sample06CancelTimeout() = runBlocking {
        try {
            withTimeout(1300L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
            }
        } catch (e: TimeoutCancellationException) {
            println("Cancellation: exception ${e.message}")
        }
    }

    private fun sample05CancelTimeout() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                withContext(NonCancellable) {
                    println("job: I'm running finally")
                    delay(1000L)
                    println("job: And I've just delayed for 1 sec because I'm non-cancellable")
                }
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        println("main: Now I can quit.")
    }

    private fun sample04CancelTimeout() = runBlocking {
        val job = launch {
            try {
                repeat(1000) {
                    println("job: I'm sleeping $it...")
                    delay(500L)
                }
            } catch (e: CancellationException) {
                println("job: caught ${e.message}")
            } finally {
                println("job: I'm running finally")
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

    private fun sample03CancelTimeout() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (isActive) { // computation loop, just wastes CPU
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        println("main: Now I can quit.")
    }

    private fun sample02CancelTimeout() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) { // computation loop, just wastes CPU
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        println("main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        println("main: Now I can quit.")
    }

    private fun sample01CancelTimeout() = runBlocking {
        val job = launch {
            repeat(1000) {
                println("job: I'm sleeping $it...")
                delay(500L)
            }
        }
        delay(2300L)
        println("main: I'm tired of waiting!")

        job.cancel()
        job.join()
        println("main: I can quit now")
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private fun sample01Basics() {
        runBlocking {
            GlobalScope.launch {
                delay(1000L)
                println("world!")
            }
            println("Hello,")
        }
    }

    private fun sample02Basics() = runBlocking {
        launch {
            delay(200L)
            println("Task from runBlocking")
        }

        coroutineScope {
            launch {
                delay(500L)
                println("Task from nested launch")
            }
            delay(100L)
            println("Task from coroutine scope")
        }

        println("Coroutine scope is over")
    }

    private fun sample03Basics() = runBlocking {
        launch {
            doWorld()
        }
        println("Hello,")
    }

    private suspend fun doWorld() {
        delay(1000L)
        println("world!")
    }

    private fun sample04Basics() = runBlocking {
        repeat(1000) {
            launch {
                delay(2000L)
                println("$it .")
            }
        }
    }

    private fun sample05Basics() = runBlocking {
        GlobalScope.launch {
            repeat(100) { i ->
                println("I'm sleeping... $i")
                delay(400L)
            }
        }
        delay(1500L)
        println("Quit global scope after 1500L")
    }
}

