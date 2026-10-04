package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    TAMIL("ta", "Tamil", "தமிழ்")
}

object Strings {
    private val en = mapOf(
        "app_title" to "TN CM Helpline • Mudhalvarin Mugavari",
        "app_subtitle" to "Civic Connect & Grievance Redressal Portal",
        "dashboard" to "Dashboard",
        "grievances" to "Petitions",
        "cm_ai" to "CM AI",
        "map_hotspots" to "Map Hotspots",
        "rti" to "RTI Act",
        "profile" to "Profile & Help",
        "admin" to "Admin Portal",
        "file_petition" to "File New Petition",
        "search_placeholder" to "Search by Tracking ID, Category, Ward...",
        "language" to "Language / மொழி",
        "select_language" to "Select Application Language",
        "english" to "English",
        "tamil" to "தமிழ் (Tamil)",
        "sla_active" to "SLA Tracker Active",
        "sla_breach" to "SLA Breach Alert",
        "status_submitted" to "Submitted",
        "status_in_progress" to "In Progress",
        "status_resolved" to "Resolved",
        "status_escalated" to "Escalated to Collector",
        "upvote" to "Upvote",
        "upvoted" to "Upvoted",
        "login" to "Sign In",
        "register" to "Register",
        "logout" to "Log Out",
        "email" to "Email Address / Mobile Number",
        "password" to "Password",
        "full_name" to "Full Name",
        "ward_code" to "Ward / District Code",
        "phone" to "Phone Number",
        "role" to "User Role",
        "google_signin" to "Sign in with Google",
        "phone_verify" to "Phone Verification / OTP",
        "forgot_password" to "Forgot Password?",
        "mfa_notice" to "Multi-Factor Authentication (MFA) Protected",
        "generate_rti_pdf" to "Generate RTI Complaint PDF",
        "ai_refiner" to "Smart AI Petition Refiner",
        "officer_directory" to "Sector Officer Directory",
        "how_to_file" to "How to File a Complaint",
        "contact_officers" to "Official Ways to Approach Officers",
        "copy_text" to "Copy Text",
        "copied" to "Copied to Clipboard!",
        "offline_mode" to "Offline Storage Mode Active",
        "bank_security" to "Government Security Standard",
        "security_standard" to "Government Security Standard & SHA-256 Encryption",
        "citizen_login" to "Citizen Portal",
        "officer_login" to "Officer Portal",
        "admin_login" to "Admin Portal",
        "enter_mobile" to "Enter 10-Digit Mobile Number",
        "send_otp" to "Send OTP Code",
        "enter_otp" to "Enter 6-Digit Verification OTP",
        "verify_otp" to "Verify OTP & Sign In",
        "citizen_desc" to "Public Petitions & CM Helpline Services",
        "officer_desc" to "Ward Officer & SLA Resolution Portal",
        "admin_desc" to "District Collectorate & System Control",
        "govt_schemes" to "TN Govt Schemes & Alerts",
        "view_all" to "View All",
        "submit_success" to "Petition Submitted Successfully"
    )

    private val ta = mapOf(
        "app_title" to "தமிழ்நாடு முதலமைச்சரின் முகவரி - 1100",
        "app_subtitle" to "மக்கள் குறைதீர்ப்பு மற்றும் சேவை இணையம்",
        "dashboard" to "முகப்பு",
        "grievances" to "மனுக்கள்",
        "cm_ai" to "முதலமைச்சர் AI",
        "map_hotspots" to "வரைபட மையம்",
        "rti" to "தகவல் அறியும் உரிமைச் சட்டம் (RTI)",
        "profile" to "சுயவிவரம் & உதவி",
        "admin" to "நிர்வாகி பக்கம்",
        "file_petition" to "புதிய மனு தாக்கல் செய்க",
        "search_placeholder" to "மனு எண், பிரிவு, வார்டு மூலமாகத் தேடுக...",
        "language" to "மொழி / Language",
        "select_language" to "செயலி மொழியைத் தேர்ந்தெடுக்கவும்",
        "english" to "English",
        "tamil" to "தமிழ் (Tamil)",
        "sla_active" to "SLA காலக்கெடு கண்காணிக்கப்படுகிறது",
        "sla_breach" to "SLA காலக்கெடு மீறல் எச்சரிக்கை",
        "status_submitted" to "சமர்ப்பிக்கப்பட்டது",
        "status_in_progress" to "செயல்பாட்டில் உள்ளது",
        "status_resolved" to "தீர்வு காணப்பட்டது",
        "status_escalated" to "மாவட்ட ஆட்சியருக்கு அனுப்பப்பட்டது",
        "upvote" to "ஆதரவளி",
        "upvoted" to "ஆதரவளிக்கப்பட்டது",
        "login" to "உள்நுழைக",
        "register" to "பதிவு செய்க",
        "logout" to "வெளியேறுக",
        "email" to "மின்னஞ்சல் / கைபேசி எண்",
        "password" to "கடவுச்சொல்",
        "full_name" to "முழு பெயர்",
        "ward_code" to "வார்டு / மாவட்ட குறியீடு",
        "phone" to "கைபேசி எண்",
        "role" to "பங்கு (Role)",
        "google_signin" to "கூகுள் கணக்கு மூலம் உள்நுழைக",
        "phone_verify" to "கைபேசி எண் சரிபார்ப்பு (OTP)",
        "forgot_password" to "கடவுச்சொல் மறந்துவிட்டதா?",
        "mfa_notice" to "இரட்டை அடுக்கு பாதுகாப்பு (MFA) இயங்குகிறது",
        "generate_rti_pdf" to "RTI மனு PDF உருவாக்குக",
        "ai_refiner" to "AI மனு திருத்தும் வசதி",
        "officer_directory" to "துறை அதிகாரிகள் விவரக் குறிப்பேடு",
        "how_to_file" to "புகார் அளிப்பது எப்படி?",
        "contact_officers" to "அதிகாரிகளைத் தொடர்பு கொள்ளும் வழிகள்",
        "copy_text" to "நகலெடு",
        "copied" to "நகலெடுக்கப்பட்டது!",
        "offline_mode" to "ஆஃப்லைன் சேமிப்பு முறை இயங்குகிறது",
        "bank_security" to "அரசு பாதுகாப்பு நெறிமுறை",
        "security_standard" to "அரசு பாதுகாப்பு நெறிமுறை & SHA-256 குறியாக்கம்",
        "citizen_login" to "பொதுமக்கள் பகுதி",
        "officer_login" to "அதிகாரிகள் பகுதி",
        "admin_login" to "நிர்வாகி பகுதி",
        "enter_mobile" to "10-இலக்க கைபேசி எண்ணை உள்ளிடவும்",
        "send_otp" to "OTP பெறுக",
        "enter_otp" to "6-இலக்க சரிபார்ப்பு OTP உள்ளிடவும்",
        "verify_otp" to "சரிபார்த்து உள்நுழைக",
        "citizen_desc" to "பொதுமக்கள் மனுக்கள் மற்றும் முதல்வர் முகவரி சேவைகள்",
        "officer_desc" to "வார்டு அதிகாரி மற்றும் குறைதீர்ப்பு பகுதி",
        "admin_desc" to "மாவட்ட ஆட்சியரகம் மற்றும் நிர்வாகக் கட்டுப்பாடு",
        "govt_schemes" to "தமிழ்நாடு அரசு திட்டங்கள் & அறிவிப்புகள்",
        "view_all" to "அனைத்தையும் காண்க",
        "submit_success" to "மனு வெற்றிகரமாக சமர்ப்பிக்கப்பட்டது"
    )

    fun get(key: String, language: AppLanguage): String {
        val map = if (language == AppLanguage.TAMIL) ta else en
        return map[key] ?: en[key] ?: key
    }
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

@Composable
fun localizedString(key: String): String {
    val language = LocalAppLanguage.current
    return Strings.get(key, language)
}
