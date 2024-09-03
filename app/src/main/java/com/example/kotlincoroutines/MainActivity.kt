package com.example.kotlincoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val COROUTINE = "COROUTINE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coroutineScope()
        coroutineDispatchers()
        coroutineSuspendModifier()
        coroutinesBuilders()
        coroutinesCancellation()
        coroutinesBlockingBuilders()
    }

    /**

     * COROUTINE SCOPE

     * Coroutine scope is nothing but a lifetime of a coroutine just like activity life time or fragment life time.
     * There are many pre-defined scopes available for kotlin coroutines.
     * For example, GlobalScope,MainScope,CoroutineScope.
     * Each a every scope has it own life span e.g. Global Scope has a life span of the entire application life cycle.

     **/
    private fun coroutineScope(){

        GlobalScope.launch {
            Log.e(COROUTINE," Scope 1 thread ----> ${Thread.currentThread().name}")
        }

        CoroutineScope(Dispatchers.Main).launch {
            Log.e(COROUTINE," Scope 2 thread ----> ${Thread.currentThread().name}")
        }

        MainScope().launch {
            Log.e(COROUTINE," Scope 3 thread ----> ${Thread.currentThread().name}")
        }

        lifecycleScope.launch(Dispatchers.Default) {
            Log.e(COROUTINE," Scope 4 thread ----> ${Thread.currentThread().name}")
        }

    }

    /**

     * COROUTINE DISPATCHERS

     * Dispatchers are nothing but thread pools.
     * Coroutines run on top of the theres so we can define dispatchers as the way to define thread on which coroutines are executed.
     * The Three main dispatchers are:
     * Dispatchers.Main
     * Dispatchers.IO
     * Dispatchers.Default

     **/
    private fun coroutineDispatchers(){

        GlobalScope.launch(Dispatchers.Main) {
            Log.e(COROUTINE," Dispatcher 1 thread ----> ${Thread.currentThread().name}")
        }

        CoroutineScope(Dispatchers.IO).launch {
            Log.e(COROUTINE," Dispatcher 2 thread ----> ${Thread.currentThread().name}")
        }

        MainScope().launch(Dispatchers.Default) {
            Log.e(COROUTINE," Dispatcher 3 thread ----> ${Thread.currentThread().name}")
        }

    }

    /**

     * SUSPEND FUNCTIONS

     * Suspend function made with suspend modifier
     * Suspend function helps in suspending coroutine computation at a particular point.
     * Suspend function must be called inside coroutine or from another suspending function.

     **/
    private fun coroutineSuspendModifier(){

        CoroutineScope(Dispatchers.Main).launch{
            runTask1()
            runTask2()
        }

    }

    suspend fun runTask1(){
        Log.e(COROUTINE," Suspend task 1 ----> execution start")
        yield()
        Log.e(COROUTINE," Suspend task 1 ----> execution completed")
    }

    suspend fun runTask2(){
        Log.e(COROUTINE," Suspend task 2 ----> execution start")
        yield()
        Log.e(COROUTINE," Suspend task 2 ----> execution completed")
    }

    /**

     * COROUTINES BUILDER

     * From the name we can define builders as a function that build the coroutines.
     * The two types are launch and async.
     * The major difference between launch and async is the return type.
     * launch returns a job object from which we can use some built in functionality.
     * async returns result as a type of last executed line.
     * Use Launch when you do not care about the result. (Fire & Forget)
     * Use Async when you expect result/output from your coroutine

     **/
    private fun coroutinesBuilders(){

        CoroutineScope(Dispatchers.Main).launch {

            var facebookCount:Int = 0

            //this return Job object from which we can use join method to wait for the result
            val job = CoroutineScope(Dispatchers.Main).launch {
                facebookCount = getFaceBookLikes()
            }
            job.join()

            Log.e(COROUTINE,"Facebook likes ----> ${facebookCount}")

            //data type of job2 is of Int type as the last statement inside scope is of Int  type
            val job2 = CoroutineScope(Dispatchers.Main).async {
                getInstagramLikes()
            }
            Log.e(COROUTINE,"Facebook likes ----> ${job2.await()}")
        }

    }

    private suspend fun getFaceBookLikes():Int{
        delay(1000)
        return 55
    }

    private suspend fun getInstagramLikes():Int{
        delay(1000)
        return 115
    }

    /**

     * COROUTINES CANCELLATION

     * To cancel coroutine we call job.cancel().
     * When there are child coroutines present inside parent coroutines and we cancel parent coroutines then by default all the child coroutines got cancelled.
     * In some scenario coroutines might not get cancelled despite calling job.cancel() method because thread becomes busy computing long running task.
     * In that case we need to check the state of the coroutine in is in active state then we need to do all the computing.

     **/
    private fun coroutinesCancellation(){
        GlobalScope.launch(Dispatchers.Main) {
            cancelCoroutine()
        }
    }

    private suspend fun cancelCoroutine(){
        val parentJob = GlobalScope.launch(Dispatchers.IO) {

            val childJob = launch {
                if (isActive){
                    for (i in 1..1000) {
                        executeLongTask()
                        Log.e(COROUTINE, "Output ----> $i")
                    }
                }
            }

            delay(3000)
            childJob.cancel()
            Log.e(COROUTINE,"Child job canceled")
            childJob.join()
            Log.e(COROUTINE,"Child job completed")

        }

        delay(1000)
        parentJob.cancel()
        Log.e(COROUTINE,"Parent job canceled")
        parentJob.join()
        Log.e(COROUTINE,"Parent job completed")
    }

    private fun executeLongTask() {
        for (i in 1..10000000){

        }
    }

    /**

     * COROUTINES BLOCKING BUILDERS

     * COROUTINES WITH CONTEXT AND RUN BLOCKING
     * The two useful coroutine builder are runBlocking and withContext.
     * Both these builder are blocking nature by default.
     * When we need to switch work to another thread then we use withContext.
     * When we need to keep alive thread then we can use runBlocking.

     **/
    private fun coroutinesBlockingBuilders(){

        runBlocking {
            delay(1000)
            Log.e(COROUTINE,"Inside run blocking")
        }
        Log.e(COROUTINE,"Run blocking completed")

        GlobalScope.launch(Dispatchers.IO) {

            Log.e(COROUTINE,"parent context ----> $coroutineContext")

            withContext(Dispatchers.Main){
                Log.e(COROUTINE,"child context ----> $coroutineContext")
            }

        }

    }

}