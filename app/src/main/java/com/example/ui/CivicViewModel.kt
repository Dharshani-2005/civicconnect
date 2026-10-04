package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CivicDatabase
import com.example.data.CivicRepository
import com.example.data.model.AuditLog
import com.example.data.model.Escalation
import com.example.data.model.Grievance
import com.example.data.model.PublicProject
import com.example.data.model.RTIRequest
import com.example.data.model.User
import com.example.data.model.Ward
import com.example.service.DataSeedService
import com.example.service.GeohashUtil
import com.example.service.PdfGeneratorService
import com.example.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.util.AppLanguage
import java.io.File

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class ShowDuplicateWarning(
        val matches: List<Pair<Grievance, Double>>,
        val pendingData: PendingGrievanceData
    ) : UiEvent()
    data class RtiPdfGenerated(val file: File) : UiEvent()
    object NavigateToHome : UiEvent()
    object NavigateToPetitions : UiEvent()
}

data class PendingGrievanceData(
    val title: String,
    val description: String,
    val category: String,
    val lat: Double,
    val lng: Double,
    val anonymous: Boolean,
    val wardCode: String,
    val photoPath: String? = null
)

class CivicViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CivicDatabase.getDatabase(application)
    val repository = CivicRepository(db)
    val session = SessionManager(application)

    // Current User Session State
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Language Preference State
    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
    }

    // Data Flows
    val allWards: StateFlow<List<Ward>> = repository.allWards
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allGrievances: StateFlow<List<Grievance>> = repository.allGrievances
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allProjects: StateFlow<List<PublicProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allRTIRequests: StateFlow<List<RTIRequest>> = repository.allRTIRequests
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allAuditLogs: StateFlow<List<AuditLog>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Live Fleet Tracking Stream
    val liveFleet: StateFlow<List<com.example.data.model.FleetVehicle>> = com.example.service.FleetTrackingService.getLiveFleetStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.service.FleetTrackingService.initialVehicles)

    // Push Notifications & Heads-up Banner
    val notifications: StateFlow<List<com.example.data.model.CivicNotification>> = com.example.service.NotificationService.notifications
    val currentPushBanner: StateFlow<com.example.data.model.CivicNotification?> = com.example.service.NotificationService.currentPushBanner

    // Pre-filled Grievance data from AI Computer Vision
    private val _prefilledGrievance = MutableStateFlow<PendingGrievanceData?>(null)
    val prefilledGrievance: StateFlow<PendingGrievanceData?> = _prefilledGrievance.asStateFlow()

    // Track most recently filed grievance so Petitions page can highlight it
    private val _lastSubmittedGrievance = MutableStateFlow<Grievance?>(null)
    val lastSubmittedGrievance: StateFlow<Grievance?> = _lastSubmittedGrievance.asStateFlow()

    fun setPrefilledGrievance(title: String, desc: String, category: String, photoPath: String?) {
        val normCategory = when {
            category.uppercase().contains("ROAD") -> "ROAD"
            category.uppercase().contains("WATER") -> "WATER"
            category.uppercase().contains("SANITATION") || category.uppercase().contains("GARBAGE") -> "SANITATION"
            category.uppercase().contains("ELECTRIC") || category.uppercase().contains("LIGHT") -> "ELECTRICITY"
            category.uppercase() in listOf("ROAD", "WATER", "SANITATION", "ELECTRICITY", "OTHER") -> category.uppercase()
            else -> "ROAD"
        }
        _prefilledGrievance.value = PendingGrievanceData(
            title = title,
            description = desc,
            category = normCategory,
            lat = 13.0418,
            lng = 80.2341,
            anonymous = false,
            wardCode = session.getWardCode().ifBlank { "CHN-W-001" },
            photoPath = photoPath
        )
    }

    fun clearPrefilledGrievance() {
        _prefilledGrievance.value = null
    }

    // AI Communication Assistant State
    private val _aiResponse = MutableStateFlow<String>("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow<Boolean>(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun refinePetitionWithAi(rawDraft: String, category: String, wardName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isAiLoading.value = true
            val prompt = "Refine the following citizen grievance draft for ward $wardName into a formal, structured petition for Tamil Nadu CM Special Cell / Mudhalvarin Mugavari:\n\nCategory: $category\nDraft: $rawDraft"
            val systemInstruction = "You are the official Tamil Nadu CM Special Cell (1100 Helpline) AI Petition Refiner. Format grievances professionally with legal context, statutory SLAs, and clear prayer for relief."
            val result = com.example.service.GeminiAiService.generateContent(prompt, systemInstruction)
            _aiResponse.value = result
            _isAiLoading.value = false
        }
    }

    fun chatWithAiAssistant(userQuery: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isAiLoading.value = true
            val prompt = userQuery
            val systemInstruction = "You are Mughavari AI Assistant, an interactive citizen helpline assistant for Tamil Nadu Municipal Governance, CM Helpline 1100, RTI Act 2005, and civic grievance resolution. Provide clear, empathetic, and accurate responses."
            val result = com.example.service.GeminiAiService.generateContent(prompt, systemInstruction)
            _aiResponse.value = result
            _isAiLoading.value = false
        }
    }

    fun translateAndSummarizeWithAi(text: String, targetLang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isAiLoading.value = true
            val prompt = "Translate the following petition to $targetLang and provide a 3-bullet point executive summary:\n\n$text"
            val result = com.example.service.GeminiAiService.generateContent(prompt)
            _aiResponse.value = result
            _isAiLoading.value = false
        }
    }

    // UI Event Channel
    private val _eventFlow = MutableSharedFlow<UiEvent>(extraBufferCapacity = 16)
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    // Selected Detail Grievance
    private val _selectedGrievance = MutableStateFlow<Grievance?>(null)
    val selectedGrievance: StateFlow<Grievance?> = _selectedGrievance.asStateFlow()

    private val _escalations = MutableStateFlow<List<Escalation>>(emptyList())
    val escalations: StateFlow<List<Escalation>> = _escalations.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            DataSeedService.seedIfNeeded(db)
            if (session.isLoggedIn()) {
                val u = db.userDao().getById(session.getUserId())
                _currentUser.value = u
            }
        }
    }

    // Auth Actions
    fun login(email: String, passwordRaw: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.loginUser(email, passwordRaw)
            res.onSuccess { user ->
                session.saveSession(user.id, user.name, user.email, user.role, user.wardCode)
                _currentUser.value = user
                _eventFlow.emit(UiEvent.ShowSnackbar("Welcome back, ${user.name}!"))
                withContext(Dispatchers.Main) {
                    onResult(true)
                }
            }.onFailure { err ->
                _eventFlow.emit(UiEvent.ShowSnackbar(err.message ?: "Login failed"))
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

    fun loginWithOtp(phone: String, role: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.loginByPhoneOrRole(phone, role)
            res.onSuccess { user ->
                session.saveSession(user.id, user.name, user.email, user.role, user.wardCode)
                _currentUser.value = user
                _eventFlow.emit(UiEvent.ShowSnackbar("Welcome back, ${user.name}!"))
                withContext(Dispatchers.Main) {
                    onResult(true)
                }
            }.onFailure { err ->
                _eventFlow.emit(UiEvent.ShowSnackbar(err.message ?: "OTP Login failed"))
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

    fun register(name: String, email: String, passwordRaw: String, role: String, wardCode: String, phone: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.registerUser(name, email, passwordRaw, role, wardCode, phone)
            res.onSuccess { user ->
                session.saveSession(user.id, user.name, user.email, user.role, user.wardCode)
                _currentUser.value = user
                _eventFlow.emit(UiEvent.ShowSnackbar("Account created successfully!"))
                withContext(Dispatchers.Main) {
                    onResult(true)
                }
            }.onFailure { err ->
                _eventFlow.emit(UiEvent.ShowSnackbar(err.message ?: "Registration failed"))
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

    fun logout() {
        session.logout()
        _currentUser.value = null
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowSnackbar("Logged out successfully"))
        }
    }

    // Grievance Actions
    fun checkAndSubmitGrievance(
        title: String,
        description: String,
        category: String,
        lat: Double,
        lng: Double,
        anonymous: Boolean,
        wardCode: String,
        photoPath: String? = null,
        forceSubmit: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!forceSubmit) {
                val duplicates = repository.checkDuplicate(title, description, category, lat, lng)
                if (duplicates.isNotEmpty()) {
                    val pending = PendingGrievanceData(title, description, category, lat, lng, anonymous, wardCode, photoPath)
                    _eventFlow.emit(UiEvent.ShowDuplicateWarning(duplicates, pending))
                    return@launch
                }
            }
            performSubmission(title, description, category, lat, lng, anonymous, wardCode, photoPath)
        }
    }

    fun performSubmission(
        title: String,
        description: String,
        category: String,
        lat: Double,
        lng: Double,
        anonymous: Boolean,
        wardCode: String,
        photoPath: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val citizenId = session.getUserId()
            val grievance = repository.submitGrievance(
                title, description, category, lat, lng, anonymous, citizenId, wardCode, photoPath
            )
            _lastSubmittedGrievance.value = grievance
            _eventFlow.emit(UiEvent.NavigateToPetitions)
            _eventFlow.emit(UiEvent.ShowSnackbar("Petition filed! Tracking ID: ${grievance.trackingId}"))
        }
    }

    fun upvoteGrievance(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upvoteGrievance(id)
            _eventFlow.emit(UiEvent.ShowSnackbar("Upvoted complaint! Category priority score boosted."))
            val updated = repository.getGrievanceById(id)
            if (_selectedGrievance.value?.id == id) {
                _selectedGrievance.value = updated
            }
        }
    }

    fun selectGrievance(grievance: Grievance) {
        _selectedGrievance.value = grievance
        viewModelScope.launch(Dispatchers.IO) {
            _escalations.value = repository.getEscalationsForGrievance(grievance.id)
        }
    }

    fun updateStatus(id: Int, newStatus: String, note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGrievanceStatus(id, newStatus, note)
            val updated = repository.getGrievanceById(id)
            _selectedGrievance.value = updated
            _eventFlow.emit(UiEvent.ShowSnackbar("Status updated to $newStatus"))
        }
    }

    fun runSLACheck() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = repository.runSLACheck()
            if (count > 0) {
                _eventFlow.emit(UiEvent.ShowSnackbar("SLA Breach Check Complete: $count complaints escalated to next authority!"))
            } else {
                _eventFlow.emit(UiEvent.ShowSnackbar("SLA Check Complete: No overdue breaches found."))
            }
        }
    }

    // RTI Actions
    fun submitRTI(subject: String, department: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val citizenId = session.getUserId()
            val citizenName = session.getUserName()
            val rti = repository.submitRTIRequest(citizenId, citizenName, subject, department, description)

            val pdfFile = PdfGeneratorService.generateRTIPdf(getApplication(), rti)
            repository.updateRTIPdfPath(rti.id, pdfFile.absolutePath)

            _eventFlow.emit(UiEvent.ShowSnackbar("RTI Application generated under RTI Act 2005!"))
            _eventFlow.emit(UiEvent.RtiPdfGenerated(pdfFile))
        }
    }

    fun submitRTIPathUpdate(id: Int, pdfPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRTIPdfPath(id, pdfPath)
        }
    }

    // K-Means Hotspot Centroids Calculation
    fun getHotspotCentroids(grievances: List<Grievance>): List<Pair<Double, Double>> {
        val points = grievances.filter { it.latitude != 0.0 && it.longitude != 0.0 }
            .map { Pair(it.latitude, it.longitude) }
        return GeohashUtil.kMeans(points, k = 5, maxIter = 50)
    }
}
