package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.SmsDispatchResult
import com.example.service.SmsGatewayService
import com.example.service.SmsProvider
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SuccessEmerald
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsGatewayBottomSheet(
    onDismiss: () -> Unit,
    onConfigSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedProvider by remember { mutableStateOf(SmsGatewayService.getSavedProvider(context)) }
    var apiKey by remember { mutableStateOf(SmsGatewayService.getSavedApiKey(context)) }
    var senderId by remember { mutableStateOf(SmsGatewayService.getSavedSenderId(context)) }
    var customUrl by remember { mutableStateOf(SmsGatewayService.getSavedCustomUrl(context)) }
    var showApiKey by remember { mutableStateOf(false) }

    var isProviderDropdownExpanded by remember { mutableStateOf(false) }
    var isTestingApi by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<SmsDispatchResult?>(null) }
    var testPhoneNumber by remember { mutableStateOf("9876543210") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SettingsPhone, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SMS Gateway API Settings",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                        Text(
                            text = "Configure REST APIs to deliver SMS to physical phones",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Provider Dropdown
            Text(
                text = "SELECT SMS GATEWAY API PROVIDER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(6.dp))

            ExposedDropdownMenuBox(
                expanded = isProviderDropdownExpanded,
                onExpandedChange = { isProviderDropdownExpanded = !isProviderDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedProvider.displayName,
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.CloudSync, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isProviderDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenu(
                    expanded = isProviderDropdownExpanded,
                    onDismissRequest = { isProviderDropdownExpanded = false }
                ) {
                    SmsProvider.values().forEach { provider ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(provider.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(provider.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            },
                            onClick = {
                                selectedProvider = provider
                                isProviderDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Provider description notice
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (selectedProvider) {
                            SmsProvider.CLOUD_OTP_GATEWAY -> "Official zero-config Cloud SMS Relay & System SMS Notification channel. Works immediately without requiring a paid API key."
                            SmsProvider.FAST2SMS -> "Fast2SMS delivers quick transactional OTPs to Indian mobile numbers via `fast2sms.com/dev/bulkV2`."
                            SmsProvider.TWO_FACTOR -> "2Factor.in is an Indian SMS gateway delivering OTPs via `2factor.in/v1/API/V1`."
                            SmsProvider.TEXTLOCAL -> "Textlocal sends domestic Indian SMS with DLT Sender IDs via `api.textlocal.in`."
                            SmsProvider.TWILIO -> "Twilio delivers SMS globally. Enter credentials in format: AccountSid:AuthToken"
                            SmsProvider.CUSTOM_WEBHOOK -> "Dispatches HTTP POST request with phone, otp, and message JSON to your URL."
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom URL if custom webhook
            if (selectedProvider == SmsProvider.CUSTOM_WEBHOOK) {
                OutlinedTextField(
                    value = customUrl,
                    onValueChange = { customUrl = it },
                    label = { Text("Webhook / Gateway Endpoint URL") },
                    placeholder = { Text("https://your-api.com/send-sms") },
                    leadingIcon = { Icon(Icons.Default.Http, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // API Key Input (optional for Cloud OTP Gateway)
            if (selectedProvider != SmsProvider.CLOUD_OTP_GATEWAY) {
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = {
                        Text(
                            if (selectedProvider == SmsProvider.TWILIO)
                                "Twilio Credentials (AccountSid:AuthToken)"
                            else
                                "${selectedProvider.displayName} API Key"
                        )
                    },
                    placeholder = { Text("Paste your API key here") },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle visibility"
                            )
                        }
                    },
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }

            if (selectedProvider == SmsProvider.TEXTLOCAL || selectedProvider == SmsProvider.TWILIO) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = senderId,
                    onValueChange = { senderId = it },
                    label = { Text(if (selectedProvider == SmsProvider.TWILIO) "Twilio Sender Phone Number" else "6-character DLT Sender ID") },
                    placeholder = { Text(if (selectedProvider == SmsProvider.TWILIO) "+15005550006" else "TNCMS") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save and Test Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        SmsGatewayService.saveConfig(
                            context = context,
                            provider = selectedProvider,
                            apiKey = apiKey,
                            senderId = senderId,
                            customUrl = customUrl
                        )
                        Toast.makeText(context, "✔ SMS API configuration saved!", Toast.LENGTH_SHORT).show()
                        onConfigSaved()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SAVE CONFIG", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        SmsGatewayService.saveConfig(
                            context = context,
                            provider = selectedProvider,
                            apiKey = apiKey,
                            senderId = senderId,
                            customUrl = customUrl
                        )
                        scope.launch {
                            isTestingApi = true
                            testResult = null
                            val testOtp = (100000..999999).random().toString()
                            val res = SmsGatewayService.sendOtpViaApi(context, testPhoneNumber, testOtp)
                            testResult = res
                            isTestingApi = false
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isTestingApi
                ) {
                    if (isTestingApi) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TESTING...", fontSize = 11.sp)
                    } else {
                        Icon(Icons.Default.SendToMobile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TEST API NOW", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Real-time API test results
            if (testResult != null) {
                Spacer(modifier = Modifier.height(12.dp))
                val res = testResult!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (res.isSuccess)
                            SuccessEmerald.copy(alpha = 0.12f)
                        else
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (res.isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (res.isSuccess) SuccessEmerald else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "API Test Response (${res.statusCode}): ${if (res.isSuccess) "SUCCESS" else "FAILED"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (res.isSuccess) SuccessEmerald else MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = res.message,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!res.rawResponse.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Raw Response: ${res.rawResponse}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
