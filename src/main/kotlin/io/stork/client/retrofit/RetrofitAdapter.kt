package io.stork.client.retrofit

import io.stork.client.retrofit.module.Account
import io.stork.proto.account.AccountsListRequest
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.intrinsics.startCoroutineCancellable
import kotlinx.coroutines.runBlocking
import retrofit2.*
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass

object RetrofitAdapter {
    inline fun <reified T : Any, K : Any> Retrofit.createAdapted(adapted: KClass<K>): T {
        return Proxy.newProxyInstance(
                RetrofitAdapter::class.java.getClassLoader(),
                arrayOf<Class<*>>(T::class.java),
                RetrofitInvocationHandler(this, adapted.java)
        ) as T
    }

    class RetrofitInvocationHandler(private val retrofit: Retrofit,
                                    private val retrofitClass: Class<*>): InvocationHandler {
        private val retrofitModule = retrofit.create(retrofitClass)
        private val responseHandler = ResponseHandler(retrofit)

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any? {
            val continuation = args[args.size - 1] as Continuation<Any>

            val argTypes = method.parameterTypes.dropLast(1).toTypedArray()
            val args = args.dropLast(1).toTypedArray()

            val retrofitMethod = retrofitModule.javaClass.getDeclaredMethod(method.name, *argTypes)

            val call: Call<Any> = retrofitMethod.invoke(retrofitModule, *args) as Call<Any>

            if (continuation is CancellableContinuation) {
                continuation.invokeOnCancellation {
                    call.cancel()
                }
            }

            call.enqueue(object: Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    with (responseHandler) {
                        val result = response.getResult()
                        continuation.resumeWith(result)
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
            return kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
        }
    }
}