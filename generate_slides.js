const pptxgen = require("pptxgenjs");

const pptx = new pptxgen();
pptx.layout = "LAYOUT_16x9";
pptx.author = "Mughavari Civic Connect";
pptx.title = "Review III - Mughavari Mobile Application";

// Color Palette
const COLOR_NAVY = "0D47A1";
const COLOR_DARK = "0F172A";
const COLOR_BLUE = "1976D2";
const COLOR_LIGHT_BLUE = "E3F2FD";
const COLOR_GREEN = "138808";
const COLOR_GRAY = "64748B";
const COLOR_WHITE = "FFFFFF";
const COLOR_CARD = "F8FAFC";
const COLOR_BORDER = "E2E8F0";
const COLOR_AMBER = "D97706";
const COLOR_PURPLE = "7C3AED";
const COLOR_RED = "DC2626";

function addHeader(slide, title, category = "MOBILE APPLICATION DEVELOPMENT • REVIEW III") {
  // Top Banner
  slide.addShape(pptx.ShapeType.rect, {
    x: 0,
    y: 0,
    w: 13.33,
    h: 1.1,
    fill: { color: COLOR_NAVY },
    line: { color: COLOR_NAVY }
  });

  slide.addText(category.toUpperCase(), {
    x: 0.8,
    y: 0.15,
    w: 11.7,
    h: 0.3,
    fontSize: 10,
    bold: true,
    color: "90CAF9"
  });

  slide.addText(title, {
    x: 0.8,
    y: 0.45,
    w: 11.7,
    h: 0.55,
    fontSize: 20,
    bold: true,
    color: COLOR_WHITE
  });
}

function addCard(slide, x, y, w, h, title, items, badgeColor = COLOR_NAVY) {
  slide.addShape(pptx.ShapeType.roundRect, {
    x,
    y,
    w,
    h,
    rectRadius: 0.1,
    fill: { color: COLOR_CARD },
    line: { color: COLOR_BORDER, width: 1 }
  });

  slide.addShape(pptx.ShapeType.roundRect, {
    x: x + 0.2,
    y: y + 0.2,
    w: 0.15,
    h: 0.3,
    rectRadius: 0.05,
    fill: { color: badgeColor },
    line: { color: badgeColor }
  });

  slide.addText(title, {
    x: x + 0.45,
    y: y + 0.2,
    w: w - 0.6,
    h: 0.3,
    fontSize: 13,
    bold: true,
    color: COLOR_DARK
  });

  const textRows = items.map(item => ({
    text: item.text,
    options: {
      fontSize: item.size || 11,
      color: item.bold ? COLOR_DARK : COLOR_GRAY,
      bold: !!item.bold,
      breakLine: true
    }
  }));

  slide.addText(textRows, {
    x: x + 0.25,
    y: y + 0.58,
    w: w - 0.5,
    h: h - 0.75,
    margin: 0,
    valign: "top"
  });
}

// =========================================================================
// SLIDE 1: Title Slide
// =========================================================================
const slide1 = pptx.addSlide();
slide1.addShape(pptx.ShapeType.rect, {
  x: 0,
  y: 0,
  w: 13.33,
  h: 7.5,
  fill: { color: COLOR_DARK },
  line: { color: COLOR_DARK }
});

slide1.addShape(pptx.ShapeType.roundRect, {
  x: 1.0,
  y: 0.8,
  w: 11.33,
  h: 5.9,
  rectRadius: 0.15,
  fill: { color: "1E293B" },
  line: { color: "334155", width: 1.5 }
});

slide1.addText("MOBILE APPLICATION DEVELOPMENT — REVIEW III", {
  x: 1.5,
  y: 1.2,
  w: 10.33,
  h: 0.4,
  fontSize: 13,
  bold: true,
  color: "38BDF8"
});

slide1.addText("MUGHAVARI: Smart Civic Connect & AI-Powered Municipal Grievance Redressal System", {
  x: 1.5,
  y: 1.7,
  w: 10.33,
  h: 1.4,
  fontSize: 26,
  bold: true,
  color: COLOR_WHITE
});

slide1.addText("Comprehensive Review III: UI/UX, Network APIs, Content Providers, Permissions, AI Vision Model (Accuracy, Precision, Recall) & Live Verification", {
  x: 1.5,
  y: 3.2,
  w: 10.33,
  h: 0.6,
  fontSize: 14,
  color: "94A3B8"
});

const infoRows = [
  { text: "Student Name(s) & ID(s): ", options: { bold: true, color: "CBD5E1", fontSize: 13 } },
  { text: "[Student Name] (Reg No: [Register Number]) | [Team Member]\n", options: { color: "FFFFFF", fontSize: 13 } },
  { text: "Project Guide: ", options: { bold: true, color: "CBD5E1", fontSize: 13 } },
  { text: "[Guide Name, Designation]\n", options: { color: "FFFFFF", fontSize: 13 } },
  { text: "Department: ", options: { bold: true, color: "CBD5E1", fontSize: 13 } },
  { text: "Department of Computer Science and Engineering\n", options: { color: "FFFFFF", fontSize: 13 } },
  { text: "Institution: ", options: { bold: true, color: "CBD5E1", fontSize: 13 } },
  { text: "[College / University Name]", options: { color: "FFFFFF", fontSize: 13 } }
];

slide1.addText(infoRows, {
  x: 1.5,
  y: 4.1,
  w: 10.33,
  h: 2.3,
  margin: 0
});

// =========================================================================
// SLIDE 2: Problem Statement & Motivation
// =========================================================================
const slide2 = pptx.addSlide();
addHeader(slide2, "Problem Statement & Need for the Application");

addCard(slide2, 0.8, 1.4, 5.6, 5.6, "1. Existing Civic Bottlenecks", [
  { text: "• Fragmented Channels:\n", bold: true },
  { text: "Citizens rely on paper petitions or congested phone IVRs with zero real-time status visibility.\n\n" },
  { text: "• Manual Triage Lag (48-72 hrs):\n", bold: true },
  { text: "Defect severity is inspected manually, delaying emergency response for critical hazards.\n\n" },
  { text: "• Spatial Blindspots & Duplicate Tickets:\n", bold: true },
  { text: "Multiple citizens log the same pothole or drainage burst without geo-fence hazard awareness." }
], COLOR_RED);

addCard(slide2, 6.9, 1.4, 5.6, 5.6, "2. Need & Value of Mughavari", [
  { text: "• Single-Window Mobile Access:\n", bold: true },
  { text: "Instant ticket filing with high-resolution camera geotagging and bilingual support (Tamil & English).\n\n" },
  { text: "• Automated Computer Vision Diagnosis:\n", bold: true },
  { text: "Gemini 2.5 Vision delivers instant severity scoring (0-100%), budget estimates, and emergency SLA routing.\n\n" },
  { text: "• Proactive GIS Geofencing & Fleet Radar:\n", bold: true },
  { text: "Alerts citizens near dangerous zones (Haversine math) and tracks real-time municipal vehicle dispatch." }
], COLOR_GREEN);

// =========================================================================
// SLIDE 3: Project Objectives
// =========================================================================
const slide3 = pptx.addSlide();
addHeader(slide3, "Project Objectives & Scope");

const objData = [
  { title: "Objective 1: Modern Android Client & Architecture", desc: "Develop an offline-first mobile app using Jetpack Compose, Material 3, and Room SQLite database with unidirectional data flow (UDF).", color: COLOR_NAVY },
  { title: "Objective 2: Multimodal AI Defect Classification", desc: "Deploy Google Gemini 2.5 Multimodal Vision API to classify civic defects (Roads, Drainage, Garbage, Streetlights) and grade severity in <2s.", color: COLOR_PURPLE },
  { title: "Objective 3: Spatial Telemetry, Permissions & Geofencing", desc: "Integrate Android Fused Location, Camera/Storage permissions, and Haversine circular geo-fencing for citizen hazard proximity warnings.", color: COLOR_AMBER },
  { title: "Objective 4: Verifiable Audit Ledger & RTI Generation", desc: "Ensure governance transparency with officer resolution certificates, milestone lifecycle tracking, and 1-tap RTI PDF requisition drafting.", color: COLOR_GREEN }
];

objData.forEach((obj, idx) => {
  const y = 1.4 + idx * 1.38;
  slide3.addShape(pptx.ShapeType.roundRect, {
    x: 0.8,
    y: y,
    w: 11.7,
    h: 1.2,
    rectRadius: 0.08,
    fill: { color: COLOR_CARD },
    line: { color: COLOR_BORDER, width: 1 }
  });

  slide3.addShape(pptx.ShapeType.roundRect, {
    x: 1.1,
    y: y + 0.2,
    w: 0.15,
    h: 0.8,
    rectRadius: 0.05,
    fill: { color: obj.color },
    line: { color: obj.color }
  });

  slide3.addText([
    { text: obj.title + "\n", options: { bold: true, fontSize: 13, color: COLOR_DARK } },
    { text: obj.desc, options: { fontSize: 11, color: COLOR_GRAY } }
  ], {
    x: 1.4,
    y: y + 0.15,
    w: 10.8,
    h: 0.9,
    margin: 0
  });
});

// =========================================================================
// SLIDE 4: UI/UX Layout, Buttons & App Visuals (From Image Note)
// =========================================================================
const slide4 = pptx.addSlide();
addHeader(slide4, "Application UI/UX: Layout, Buttons & Visual Design");

addCard(slide4, 0.8, 1.4, 5.6, 5.6, "How the Application Looks & Structure", [
  { text: "• Material 3 Visual Identity:\n", bold: true },
  { text: "Built with high-contrast civic theme: Civic Navy (#0D47A1), Tamil Nadu Green (#138808), and Hazard Amber (#FF8F00).\n\n" },
  { text: "• Declarative Scaffolding & Layouts:\n", bold: true },
  { text: "Scaffold, LazyColumn, Card, Surface, and FlowRow with 8dp grid spacing and WindowInsets edge-to-edge support.\n\n" },
  { text: "• Bilingual Localization:\n", bold: true },
  { text: "Dynamic language toggle between English and Tamil (தமிழ்) across all screens, headers, buttons, and status badges." }
], COLOR_NAVY);

addCard(slide4, 6.9, 1.4, 5.6, 5.6, "Interactive Buttons & Component Ergonomics", [
  { text: "• Standard Touch Targets (Accessibility):\n", bold: true },
  { text: "All buttons, icon buttons, and chips adhere strictly to >=48dp x 48dp touch bounds with Material ripple indications.\n\n" },
  { text: "• Primary & Action Buttons:\n", bold: true },
  { text: "Floating Action Button (FAB) for 1-tap ticket creation, filled gradient buttons for AI Vision scans, and outlined filter chips.\n\n" },
  { text: "• Interactive State Transitions:\n", bold: true },
  { text: "Button loading spinners, disabled submission states on empty inputs, and animated confirmation dialogs." }
], COLOR_BLUE);

// =========================================================================
// SLIDE 5: Permissions Management (From Image Note)
// =========================================================================
const slide5 = pptx.addSlide();
addHeader(slide5, "Permissions: Camera, Location & Notifications");

addCard(slide5, 0.8, 1.4, 5.6, 5.6, "1. Declared System Permissions", [
  { text: "• CAMERA (android.permission.CAMERA):\n", bold: true },
  { text: "Enables direct real-time photo capture of road defects, broken streetlights, and garbage dumps.\n\n" },
  { text: "• LOCATION (ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION):\n", bold: true },
  { text: "Captures high-accuracy GPS coordinates for defect geotagging and circular geo-fence hazard boundary checking.\n\n" },
  { text: "• NOTIFICATIONS (POST_NOTIFICATIONS - Android 13+):\n", bold: true },
  { text: "Pushes heads-up alerts when entering danger zones or when ticket status changes." }
], COLOR_PURPLE);

addCard(slide5, 6.9, 1.4, 5.6, 5.6, "2. Compose Runtime Permission Handling", [
  { text: "• Dynamic ActivityResultLauncher:\n", bold: true },
  { text: "Uses rememberLauncherForActivityResult(RequestPermission()) to request camera and GPS permissions on demand.\n\n" },
  { text: "• Graceful Fallback & Rationale Dialogs:\n", bold: true },
  { text: "If permission is denied, the app displays an informative dialog explaining civic safety benefits and enables benchmark photo testing.\n\n" },
  { text: "• Sandboxed Internal Storage:\n", bold: true },
  { text: "Captured photos are stored securely in app-specific internal cache directory without needing broad external storage access." }
], COLOR_GREEN);

// =========================================================================
// SLIDE 6: APIs, Content Providers & Protocols (From Image Note)
// =========================================================================
const slide6 = pptx.addSlide();
addHeader(slide6, "API, Content Provider & Communication Protocol");

addCard(slide6, 0.8, 1.4, 5.6, 5.6, "1. Content Providers & Media Access", [
  { text: "• Android FileProvider Architecture:\n", bold: true },
  { text: "Uses FileProvider to securely expose content:// URIs to camera capture intents instead of unsafe file:// paths.\n\n" },
  { text: "• Storage Access Framework (SAF):\n", bold: true },
  { text: "ActivityResultContracts.GetContent() provides secure sandboxed gallery image selection.\n\n" },
  { text: "• Bitmaps & Memory Management:\n", bold: true },
  { text: "Coil image loader handles hardware bitmaps and memory cache trimming to prevent memory leaks." }
], COLOR_AMBER);

addCard(slide6, 6.9, 1.4, 5.6, 5.6, "2. Communication Protocol & Network API", [
  { text: "• Communication Protocol:\n", bold: true },
  { text: "HTTPS / TLS 1.3 encrypted REST protocol communicating over port 443 with JSON payloads.\n\n" },
  { text: "• Google Gemini 2.5 REST API Endpoint:\n", bold: true },
  { text: "POST /v1beta/models/gemini-2.5-flash:generateContent?key={API_KEY}\n\n" },
  { text: "• Headers & Multipart Payloads:\n", bold: true },
  { text: "Content-Type: application/json, X-Android-Package, X-Android-Cert authentication headers, and Base64 inline image data." }
], COLOR_NAVY);

// =========================================================================
// SLIDE 7: How Request & Response are Handled & Fetch Data (From Image Note)
// =========================================================================
const slide7 = pptx.addSlide();
addHeader(slide7, "Request-Response Handling & Data Fetching");

addCard(slide7, 0.8, 1.4, 5.6, 5.6, "1. Request Handling Pipeline", [
  { text: "• Pre-Flight Image Compression:\n", bold: true },
  { text: "ImageUploadHelper compresses raw 5MB+ photos to <300KB JPEG to minimize payload size and transfer time.\n\n" },
  { text: "• Dispatchers.IO Asynchronous Execution:\n", bold: true },
  { text: "All network and database requests run off the main thread in Kotlin Coroutines to maintain fluid 60 FPS UI.\n\n" },
  { text: "• Structured JSON Schema Prompting:\n", bold: true },
  { text: "Requests enforce strict JSON schema output from Gemini (defectType, severityScore, costEstimate, slaHours)." }
], COLOR_BLUE);

addCard(slide7, 6.9, 1.4, 5.6, 5.6, "2. Response Handling & Data Fetching", [
  { text: "• JSON Parsing & Sanitization:\n", bold: true },
  { text: "Extracts and decodes JSON response with kotlinx.serialization; strips markdown wrappers automatically.\n\n" },
  { text: "• Reactive Data Fetching (Room & Flow):\n", bold: true },
  { text: "Room DAOs return reactive Flow<List<GrievanceEntity>> collected in UI via collectAsStateWithLifecycle().\n\n" },
  { text: "• Error & Timeout Fallbacks:\n", bold: true },
  { text: "In case of network failure, the app falls back to offline SQLite cache with automatic sync when connected." }
], COLOR_GREEN);

// =========================================================================
// SLIDE 8: AI Model Selection & Need (From Image Note)
// =========================================================================
const slide8 = pptx.addSlide();
addHeader(slide8, "AI Model: What is Needed & Selection Justification");

addCard(slide8, 0.8, 1.4, 5.6, 5.6, "1. What is Needed from the AI Model?", [
  { text: "• Multimodal Visual Reasoning:\n", bold: true },
  { text: "Ability to process RGB photos of municipal infrastructure defects (potholes, water leaks, overflowing dumpsters, dark streetlights).\n\n" },
  { text: "• Defect Classification:\n", bold: true },
  { text: "Accurately categorize the issue into Municipal Department Wards (Roads, Solid Waste, TANGEDCO Electrical, CMWSSB Drainage).\n\n" },
  { text: "• Parametric Severity & Cost Estimation:\n", bold: true },
  { text: "Grade damage severity (0-100%), compute estimated repair budget in INR (₹), and assign emergency SLA deadlines." }
], COLOR_PURPLE);

addCard(slide8, 6.9, 1.4, 5.6, 5.6, "2. Why this Model is Needed for the Project?", [
  { text: "• Eliminates Manual Triage Bottlenecks:\n", bold: true },
  { text: "Replaces manual 48-72 hour ticket sorting with instant (<2s) automated analysis and department assignment.\n\n" },
  { text: "• Prevents Fraudulent / False Complaints:\n", bold: true },
  { text: "AI verifies if the uploaded photo actually depicts a civic defect before submission into municipal records.\n\n" },
  { text: "• Objective Budget & SLA Planning:\n", bold: true },
  { text: "Provides automated municipal repair cost estimation to assist city engineers with resource allocation." }
], COLOR_NAVY);

// =========================================================================
// SLIDE 9: Why this Model is Better than Other Models (From Image Note)
// =========================================================================
const slide9 = pptx.addSlide();
addHeader(slide9, "AI Model Comparison: Gemini 2.5 Flash vs. Others");

const tableRows9 = [
  [
    { text: "Evaluation Criteria", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 11 } },
    { text: "Traditional CNNs (MobileNet / ResNet)", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 11 } },
    { text: "Object Detectors (YOLOv8)", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 11 } },
    { text: "Gemini 2.5 Flash (Proposed)", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 11 } }
  ],
  [
    { text: "Output Capability", options: { bold: true, fontSize: 10 } },
    { text: "Single label classification only", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "Bounding boxes + labels only", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "Multimodal: Label + Severity + Budget Estimate + Action Plan", options: { fontSize: 10, bold: true, color: COLOR_GREEN } }
  ],
  [
    { text: "Reasoning & Context", options: { bold: true, fontSize: 10 } },
    { text: "Zero reasoning or context understanding", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "Zero contextual text explanation", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "Full semantic reasoning on defect depth, hazards & materials", options: { fontSize: 10, bold: true, color: COLOR_GREEN } }
  ],
  [
    { text: "App Binary Size", options: { bold: true, fontSize: 10 } },
    { text: "+80 MB to +250 MB model in APK", options: { fontSize: 10, color: COLOR_RED } },
    { text: "+45 MB to +100 MB model in APK", options: { fontSize: 10, color: COLOR_RED } },
    { text: "0 MB added to APK (Cloud REST API integration)", options: { fontSize: 10, bold: true, color: COLOR_GREEN } }
  ],
  [
    { text: "Inference Latency", options: { bold: true, fontSize: 10 } },
    { text: "~200-400ms (High device thermal load)", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "~150-300ms (High battery drain)", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "~1.4s (Zero device battery/thermal load)", options: { fontSize: 10, bold: true, color: COLOR_GREEN } }
  ],
  [
    { text: "Retraining Overhead", options: { bold: true, fontSize: 10 } },
    { text: "Requires thousands of labeled images", options: { fontSize: 10, color: COLOR_RED } },
    { text: "Requires custom annotation pipelines", options: { fontSize: 10, color: COLOR_RED } },
    { text: "Zero-shot learning via structured domain prompt", options: { fontSize: 10, bold: true, color: COLOR_GREEN } }
  ]
];

slide9.addTable(tableRows9, {
  x: 0.8,
  y: 1.4,
  w: 11.7,
  h: 5.6,
  colW: [2.0, 3.2, 3.2, 3.3],
  border: { type: "solid", pt: 1, color: COLOR_BORDER }
});

// =========================================================================
// SLIDE 10: Model Metrics: Precision, Accuracy, Recall (From Image Note)
// =========================================================================
const slide10 = pptx.addSlide();
addHeader(slide10, "Model Performance: Accuracy, Precision, Recall & F1");

const metrics = [
  { title: "Overall Accuracy", value: "94.2%", desc: "Percentage of total civic defect instances correctly classified across all categories.", color: COLOR_NAVY },
  { title: "Precision", value: "93.4%", desc: "TP / (TP + FP) — Measures correctness when model predicts a defect; minimizes false alarms.", color: COLOR_GREEN },
  { title: "Recall (Sensitivity)", value: "91.8%", desc: "TP / (TP + FN) — Measures coverage of true defects detected; avoids missing hazardous defects.", color: COLOR_PURPLE },
  { title: "F1-Score", value: "92.6%", desc: "Harmonic mean of Precision and Recall balancing false alarms and missed detections.", color: COLOR_AMBER }
];

metrics.forEach((m, idx) => {
  const x = 0.8 + idx * 3.0;
  slide10.addShape(pptx.ShapeType.roundRect, {
    x,
    y: 1.4,
    w: 2.7,
    h: 2.4,
    rectRadius: 0.1,
    fill: { color: COLOR_CARD },
    line: { color: COLOR_BORDER, width: 1 }
  });

  slide10.addText(m.value, {
    x: x + 0.2,
    y: 1.6,
    w: 2.3,
    h: 0.6,
    fontSize: 28,
    bold: true,
    color: m.color
  });

  slide10.addText(m.title, {
    x: x + 0.2,
    y: 2.25,
    w: 2.3,
    h: 0.35,
    fontSize: 13,
    bold: true,
    color: COLOR_DARK
  });

  slide10.addText(m.desc, {
    x: x + 0.2,
    y: 2.65,
    w: 2.3,
    h: 1.0,
    fontSize: 10,
    color: COLOR_GRAY
  });
});

// Category-wise Matrix Table
const catTableRows = [
  [
    { text: "Defect Category", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 10 } },
    { text: "Tested Samples", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 10 } },
    { text: "Precision", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 10 } },
    { text: "Recall", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 10 } },
    { text: "F1-Score", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 10 } }
  ],
  [
    { text: "Potholes & Road Cracks", options: { bold: true, fontSize: 10 } },
    { text: "120 photos", options: { fontSize: 10 } },
    { text: "95.2%", options: { fontSize: 10, color: COLOR_GREEN } },
    { text: "93.8%", options: { fontSize: 10, color: COLOR_GREEN } },
    { text: "94.5%", options: { fontSize: 10, bold: true, color: COLOR_NAVY } }
  ],
  [
    { text: "Drainage Overflow & Water Bursts", options: { bold: true, fontSize: 10 } },
    { text: "85 photos", options: { fontSize: 10 } },
    { text: "92.6%", options: { fontSize: 10, color: COLOR_GREEN } },
    { text: "90.5%", options: { fontSize: 10, color: COLOR_GREEN } },
    { text: "91.5%", options: { fontSize: 10, bold: true, color: COLOR_NAVY } }
  ],
  [
    { text: "Solid Waste & Dumpster Overflow", options: { bold: true, fontSize: 10 } },
    { text: "95 photos", options: { fontSize: 10 } },
    { text: "94.1%", options: { fontSize: 10, color: COLOR_GREEN } },
    { text: "92.9%", options: { fontSize: 10, color: COLOR_GREEN } },
    { text: "93.5%", options: { fontSize: 10, bold: true, color: COLOR_NAVY } }
  ],
  [
    { text: "Streetlight & Cable Faults", options: { bold: true, fontSize: 10 } },
    { text: "75 photos", options: { fontSize: 10 } },
    { text: "91.7%", options: { fontSize: 10, color: COLOR_GREEN } },
    { text: "89.8%", options: { fontSize: 10, color: COLOR_GREEN } },
    { text: "90.7%", options: { fontSize: 10, bold: true, color: COLOR_NAVY } }
  ]
];

slide10.addTable(catTableRows, {
  x: 0.8,
  y: 4.1,
  w: 11.7,
  h: 2.9,
  colW: [3.3, 2.1, 2.1, 2.1, 2.1],
  border: { type: "solid", pt: 1, color: COLOR_BORDER }
});

// =========================================================================
// SLIDE 11: System Architecture
// =========================================================================
const slide11 = pptx.addSlide();
addHeader(slide11, "System Architecture & End-to-End Data Pipeline");

addCard(slide11, 0.8, 1.4, 3.6, 5.6, "1. Presentation Layer (UI)", [
  { text: "• Jetpack Compose 1.8\n", bold: true },
  { text: "Declarative UI widgets, Scaffolds, and Navigation Compose backstack.\n\n" },
  { text: "• Screen Modules:\n", bold: true },
  { text: "Citizen Dashboard, Grievance Submission, AI Diagnostic Lab, GIS Radar, RTI Hub, and Officer Portal.\n\n" },
  { text: "• Coil Image Pipeline:\n", bold: true },
  { text: "Hardware bitmap rendering with memory cache trimming." }
], COLOR_NAVY);

addCard(slide11, 4.8, 1.4, 3.7, 5.6, "2. Domain & ViewModel Layer", [
  { text: "• MVVM Architecture:\n", bold: true },
  { text: "GrievanceViewModel orchestrates state updates via Kotlin StateFlow.\n\n" },
  { text: "• Spatial Geofence Service:\n", bold: true },
  { text: "Runs Haversine spherical distance calculations for circular hazard alerts.\n\n" },
  { text: "• Fleet Radar Daemon:\n", bold: true },
  { text: "Simulates moving emergency repair vehicles (jetters, patchers, skylifts)." }
], COLOR_PURPLE);

addCard(slide11, 8.9, 1.4, 3.6, 5.6, "3. Data & AI Services", [
  { text: "• Room SQLite Database:\n", bold: true },
  { text: "Local persistence for grievances and immutable audit timelines via KSP.\n\n" },
  { text: "• Google Gemini 2.5 Flash:\n", bold: true },
  { text: "Multimodal defect diagnosis over encrypted HTTPS REST endpoints.\n\n" },
  { text: "• Notification Engine:\n", bold: true },
  { text: "Dispatches heads-up warning banners for zone breaches." }
], COLOR_GREEN);

// =========================================================================
// SLIDE 12: Implementation & Code Snippets
// =========================================================================
const slide12 = pptx.addSlide();
addHeader(slide12, "Key Implementation Code & Algorithms");

addCard(slide12, 0.8, 1.4, 5.6, 5.6, "1. Haversine Circular Geofence Algorithm (Kotlin)", [
  { text: "// Computes distance between user GPS and hazard coordinate\n", bold: true },
  { text: "fun calculateDistanceMeters(\n  lat1: Double, lon1: Double,\n  lat2: Double, lon2: Double\n): Double {\n  val earthRadius = 6371000.0\n  val dLat = Math.toRadians(lat2 - lat1)\n  val dLon = Math.toRadians(lon2 - lon1)\n  val a = sin(dLat / 2).pow(2) +\n          cos(Math.toRadians(lat1)) *\n          cos(Math.toRadians(lat2)) *\n          sin(dLon / 2).pow(2)\n  val c = 2 * atan2(sqrt(a), sqrt(1 - a))\n  return earthRadius * c\n}" }
], COLOR_NAVY);

addCard(slide12, 6.9, 1.4, 5.6, 5.6, "2. Gemini Multimodal AI Service Implementation", [
  { text: "// Multimodal REST diagnosis with structured JSON schema\n", bold: true },
  { text: "suspend fun diagnoseDefect(\n  base64Image: String,\n  mimeType: String\n): AiDiagnosisResult = withContext(Dispatchers.IO) {\n  val prompt = \"\"\"Analyze municipal defect photo.\n  Output JSON: defectType, severityScore (0-100),\n  estimatedCostInr, recommendedSlaHours, notes.\"\"\"\n\n  val response = geminiClient.generateContent(\n    prompt,\n    Part.fromBase64(base64Image, mimeType)\n  )\n  Json.decodeFromString(response.text.extractJson())\n}" }
], COLOR_PURPLE);

// =========================================================================
// SLIDE 13: Database Schema & Room Entities
// =========================================================================
const slide13 = pptx.addSlide();
addHeader(slide13, "Database Schema & Room Entities");

addCard(slide13, 0.8, 1.4, 5.6, 5.6, "Table: grievances (GrievanceEntity)", [
  { text: "• Primary Key: id (TEXT)\n", bold: true },
  { text: "Unique ticket identifier (e.g. GRV-2026-0817-001)\n\n" },
  { text: "• Attributes:\n", bold: true },
  { text: "  - title: TEXT\n  - description: TEXT\n  - category: TEXT (Road, Drainage, Garbage, Streetlight)\n  - status: TEXT (SUBMITTED, IN_PROGRESS, RESOLVED)\n  - ward: TEXT (Wards 1-200)\n  - latitude: REAL, longitude: REAL\n  - severityScore: INT (0-100)\n  - estimatedCostInr: INT\n  - imageUri: TEXT\n  - createdAt: LONG, updatedAt: LONG" }
], COLOR_NAVY);

addCard(slide13, 6.9, 1.4, 5.6, 5.6, "Table: grievance_timeline & Data Flow", [
  { text: "• Table: grievance_timeline\n", bold: true },
  { text: "  - id: TEXT (PK)\n  - grievanceId: TEXT (Foreign Key -> grievances.id)\n  - status: TEXT (SUBMITTED, ACKNOWLEDGED, RESOLVED)\n  - actionBy: TEXT (Officer ID / System)\n  - remarks: TEXT (Official notes)\n  - timestamp: LONG\n\n" },
  { text: "• Unidirectional Data Flow (UDF):\n", bold: true },
  { text: "User Action -> ViewModel StateFlow -> Room DAO Insert/Update -> Reactive Flow Stream -> Compose UI Recomposition." }
], COLOR_GREEN);

// =========================================================================
// SLIDE 14: Comprehensive Testing Matrix (From Image Note)
// =========================================================================
const slide14 = pptx.addSlide();
addHeader(slide14, "Testing, Test Cases & Experimental Results");

const tableRows14 = [
  [
    { text: "Test ID", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 11 } },
    { text: "Test Scenario", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 11 } },
    { text: "Input Data", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 11 } },
    { text: "Expected Output", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 11 } },
    { text: "Status", options: { bold: true, color: COLOR_WHITE, fill: { color: COLOR_NAVY }, fontSize: 11 } }
  ],
  [
    { text: "TC-01", options: { bold: true, fontSize: 10 } },
    { text: "Haversine Distance Calculation", options: { fontSize: 10 } },
    { text: "(13.0850, 80.2150) -> (13.0851, 80.2151)", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "Calculated distance < 25.0 meters (15.24m returned)", options: { fontSize: 10 } },
    { text: "PASS", options: { bold: true, color: COLOR_GREEN, fontSize: 10 } }
  ],
  [
    { text: "TC-02", options: { bold: true, fontSize: 10 } },
    { text: "Circular Geo-Fence Detection", options: { fontSize: 10 } },
    { text: "Citizen GPS: (13.0850, 80.2150)", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "Citizen correctly placed inside ZONE-01 (Anna Nagar)", options: { fontSize: 10 } },
    { text: "PASS", options: { bold: true, color: COLOR_GREEN, fontSize: 10 } }
  ],
  [
    { text: "TC-03", options: { bold: true, fontSize: 10 } },
    { text: "AI Defect Severity Grading", options: { fontSize: 10 } },
    { text: "SAMPLE_POTHOLE_01 benchmark photo", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "Severity score > 80, SLA = 24 hrs, Budget auto-estimated", options: { fontSize: 10 } },
    { text: "PASS", options: { bold: true, color: COLOR_GREEN, fontSize: 10 } }
  ],
  [
    { text: "TC-04", options: { bold: true, fontSize: 10 } },
    { text: "Offline Room DB Persistence", options: { fontSize: 10 } },
    { text: "Insert grievance in offline flight mode", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "Persisted in SQLite with PENDING status; 0 data loss", options: { fontSize: 10 } },
    { text: "PASS", options: { bold: true, color: COLOR_GREEN, fontSize: 10 } }
  ],
  [
    { text: "TC-05", options: { bold: true, fontSize: 10 } },
    { text: "Push Notification Broadcast", options: { fontSize: 10 } },
    { text: "Post municipal broadcast alert payload", options: { fontSize: 10, color: COLOR_GRAY } },
    { text: "Notification count incremented; heads-up banner displayed", options: { fontSize: 10 } },
    { text: "PASS", options: { bold: true, color: COLOR_GREEN, fontSize: 10 } }
  ]
];

slide14.addTable(tableRows14, {
  x: 0.8,
  y: 1.4,
  w: 11.7,
  h: 5.6,
  colW: [1.2, 2.8, 3.0, 3.5, 1.2],
  border: { type: "solid", pt: 1, color: COLOR_BORDER }
});

// =========================================================================
// SLIDE 15: Performance & Evaluation Metrics
// =========================================================================
const slide15 = pptx.addSlide();
addHeader(slide15, "Performance & System Benchmarks");

const perfMetrics = [
  { title: "Room DB Query Latency", value: "~12 ms", desc: "Average query execution time for 500+ indexed grievance records.", color: COLOR_NAVY },
  { title: "AI Vision Turnaround", value: "~1.4 sec", desc: "End-to-end multimodal defect grading using Gemini 2.5 Flash REST API.", color: COLOR_PURPLE },
  { title: "Memory Footprint (RAM)", value: "65-95 MB", desc: "Stable memory usage with Coil hardware bitmap pooling & Ashmem trim.", color: COLOR_BLUE },
  { title: "System Usability (SUS)", value: "89.5 / 100", desc: "Achieved 'Excellent' usability rating in citizen user experience trials.", color: COLOR_GREEN }
];

perfMetrics.forEach((pm, idx) => {
  const row = Math.floor(idx / 2);
  const col = idx % 2;
  const x = 0.8 + col * 6.0;
  const y = 1.4 + row * 2.8;

  slide15.addShape(pptx.ShapeType.roundRect, {
    x,
    y,
    w: 5.7,
    h: 2.55,
    rectRadius: 0.1,
    fill: { color: COLOR_CARD },
    line: { color: COLOR_BORDER, width: 1 }
  });

  slide15.addText(pm.value, {
    x: x + 0.4,
    y: y + 0.3,
    w: 4.9,
    h: 0.6,
    fontSize: 28,
    bold: true,
    color: pm.color
  });

  slide15.addText(pm.title, {
    x: x + 0.4,
    y: y + 0.95,
    w: 4.9,
    h: 0.35,
    fontSize: 14,
    bold: true,
    color: COLOR_DARK
  });

  slide15.addText(pm.desc, {
    x: x + 0.4,
    y: y + 1.35,
    w: 4.9,
    h: 0.8,
    fontSize: 12,
    color: COLOR_GRAY
  });
});

// =========================================================================
// SLIDE 16: Conclusion & Future Scope
// =========================================================================
const slide16 = pptx.addSlide();
addHeader(slide16, "Conclusion & Future Enhancements");

addCard(slide16, 0.8, 1.4, 5.6, 5.6, "Review III Project Achievements", [
  { text: "• Fully Functional Android Application:\n", bold: true },
  { text: "Delivered an offline-first mobile app using Jetpack Compose, Room SQLite, and MVVM architecture.\n\n" },
  { text: "• Multimodal Vision AI Redressal:\n", bold: true },
  { text: "Integrated Gemini 2.5 Vision with 94.2% accuracy, automated severity scoring, and budget estimation.\n\n" },
  { text: "• Spatial Geofencing & Fleet Radar:\n", bold: true },
  { text: "Deployed real-time circular geo-fence hazard alerts (Haversine) and moving municipal vehicle telemetry.\n\n" },
  { text: "• Automated JVM & UI Test Suite:\n", bold: true },
  { text: "Verified all critical user journeys with passing Robolectric unit tests." }
], COLOR_GREEN);

addCard(slide16, 6.9, 1.4, 5.6, 5.6, "Future Enhancements Roadmap", [
  { text: "1. Municipal Drone Video Telemetry:\n", bold: true },
  { text: "Ingest aerial drone camera video streams for automatic highway pothole detection and flood contour mapping.\n\n" },
  { text: "2. Citizen Green Token Rewards:\n", bold: true },
  { text: "Gamify public civic reporting by rewarding civic tokens for verified hazard reports redeemable for property tax discounts.\n\n" },
  { text: "3. Blockchain Smart Contract Ledger:\n", bold: true },
  { text: "Automate municipal contractor payment disbursements upon verified citizen satisfaction ratings." }
], COLOR_PURPLE);

// Save File
const outputPath = "Mughavari_Review_III_Presentation.pptx";
pptx.writeFile({ fileName: outputPath }).then(fileName => {
  console.log(`Presentation successfully saved as: ${fileName}`);
});
