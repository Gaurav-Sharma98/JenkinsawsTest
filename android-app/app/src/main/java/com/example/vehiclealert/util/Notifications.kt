package com.example.vehiclealert.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast

object NotificationsUtil {
    fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            val sms = SmsManager.getDefault()
            sms.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${'$'}phoneNumber")
                putExtra("sms_body", message)
            }
            runCatching { context.startActivity(intent) }
        } catch (_: Throwable) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${'$'}phoneNumber")
                putExtra("sms_body", message)
            }
            runCatching { context.startActivity(intent) }
        }
    }

    fun sendWhatsApp(context: Context, phoneNumber: String, message: String) {
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }
        val url = "https://wa.me/${'$'}cleaned?text=" + Uri.encode(message)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun callOwner(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:${'$'}phoneNumber")
        }
        try {
            context.startActivity(intent)
        } catch (_: SecurityException) {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${'$'}phoneNumber")
            }
            context.startActivity(dialIntent)
        }
    }
}
