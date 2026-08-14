package com.example.kampus.di

import android.content.Context
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import com.example.kampus.data.remote.SupabaseStorageManager

object SupabaseModule {

    private const val SUPABASE_URL = "https://fisddiizqkgzbehkzkfg.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZpc2RkaWl6cWtnemJlaGt6a2ZnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODYzMzk5ODcsImV4cCI6MjEwMTkxNTk4N30.TFmRiMwx9NPK2CchbNDJ3PkWsB3lR4vXa7smQTNs3Kk"

    private var supabaseClient: io.github.jan.supabase.SupabaseClient? = null
    private var storageManager: SupabaseStorageManager? = null

    fun initSupabase(context: Context) {
        if (supabaseClient == null) {
            supabaseClient = createSupabaseClient(
                supabaseUrl = SUPABASE_URL,
                supabaseKey = SUPABASE_ANON_KEY
            ) {
                install(Postgrest)
                install(Realtime)
                install(Storage)
            }
        }

        if (storageManager == null) {
            storageManager = SupabaseStorageManager(supabaseClient!!, context)
        }
    }

    fun getSupabaseClient(): io.github.jan.supabase.SupabaseClient {
        return supabaseClient ?: throw IllegalStateException("Supabase not initialized. Call initSupabase() first.")
    }

    fun getStorageManager(): SupabaseStorageManager {
        return storageManager ?: throw IllegalStateException("Supabase Storage Manager not initialized.")
    }
}
