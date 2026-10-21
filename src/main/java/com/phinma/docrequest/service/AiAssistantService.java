package com.phinma.docrequest.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class AiAssistantService {

    private final List<String> filipinoKeywords = Arrays.asList(
        "ano", "paano", "saan", "kailan", "magkano", "ba", "po", "ng", "ang", 
        "mga", "para", "saan", "to", "ito", "pede", "pwede", "bakit", "meron", "kunin"
    );

    public String getAnswer(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "Kumusta! May maitutulong ba ako sa document request mo? / Hello! How can I help you with your document request?";
        }

        String q = query.toLowerCase().trim();
        boolean isFilipino = isTagalogOrTaglish(q);

        // 1. "Para saan to?" / General System Purpose
        if (q.contains("para saan") || q.contains("what is this for") || q.contains("about the system") || q.contains("layunin")) {
            return isFilipino
                ? "Ang system na ito ay ang Saint Jude College Manila Document Request Portal. Dito pwedeng mag-request ang mga estudyante ng official school documents tulad ng Form 137, Good Moral Certificate, at Report Card online nang hindi na kailangang magpabalik-balik sa campus para lang mag-apply o mag-follow up."
                : "This system is the official Saint Jude College Manila Document Request Portal. It allows students to request official academic documents (such as Form 137, Good Moral Certificates, and Report Cards) online without having to visit the Registrar repeatedly.";
        }

        // 2. Form 137 / Transcript
        if (q.contains("form 137") || q.contains("transcript") || q.contains("tor")) {
            return isFilipino
                ? "Ang Form 137 o Permanent Academic Record ay karaniwang kinakailangan sa pag-transfer ng school o sa higher education requirements. Maaari mo itong piliin sa 'New Request' tab. Karaniwang tumatagal ang proseso nito ng 3 hanggang 5 working days bago maging 'Ready for Pick-up'."
                : "Form 137 (Transcript of Records/Permanent Academic Record) is used for school transfers, employment, or academic evaluations. You can request it via the 'New Request' tab. Standard processing time is 3–5 working days.";
        }

        // 3. Good Moral Certificate
        if (q.contains("good moral") || q.contains("moral")) {
            return isFilipino
                ? "Ang Certificate of Good Moral Character ay patunay ng iyong magandang record sa eskwelahan. Kalimitang kailangan ito sa scholarship, board exams, at trabaho. Siguraduhing wala kang pending disciplinary hold bago mag-request."
                : "The Certificate of Good Moral Character certifies your conduct record at Saint Jude College. It is typically required for scholarships, employment, or board exams. Please ensure you have no student affairs holds.";
        }

        // 4. Report Card / Grade Slip
        if (q.contains("report card") || q.contains("grades") || q.contains("grade slip")) {
            return isFilipino
                ? "Ang Official Report Card / Grade Slip ay nagpapakita ng iyong academic grades para sa partikular na semester o academic year. Piliin lamang ito sa dropdown at ilagay ang layunin ng iyong paghingi."
                : "The Official Report Card or Grade Slip reflects your final grades for the term. Choose this in the 'Select Document Type' menu and state your academic purpose.";
        }

        // 5. Pick-up / Claiming Instructions
        if (q.contains("claim") || q.contains("kunin") || q.contains("pick up") || q.contains("pickup") || q.contains("saan kukunin")) {
            return isFilipino
                ? "Ang lahat ng official physical documents na may dry seal at opisyal na lagda ay personal na kinukuha sa Saint Jude College Registrar's Office (Dimasalang cor. Don Quijote St., Sampaloc, Manila). Hintayin lamang na maging 'Ready for Pick-up' ang status sa iyong dashboard at dalhin ang iyong PHINMA Student ID."
                : "All official physical documents bearing the dry seal and signature must be claimed in person at the Saint Jude College Registrar's Office (Dimasalang Campus, Sampaloc, Manila). Please wait until your status displays 'Ready for Pick-up' and bring your valid PHINMA Student ID.";
        }

        // 6. Payment / Fees
        if (q.contains("bayad") || q.contains("payment") || q.contains("magkano") || q.contains("fee") || q.contains("gcash")) {
            return isFilipino
                ? "Alinsunod sa patakaran ng campus, walang online payment gateway tulad ng GCash. Kung may kaakibat na bayarin ang dokumentong hinihiling, ito ay babayaran over-the-counter sa School Cashier bago ang pag-release ng dokumento."
                : "In accordance with campus policies, online payment gateways are not integrated. Any required document fees are settled over-the-counter at the School Cashier on campus prior to claiming.";
        }

        // Default response
        return isFilipino
            ? "Maaari kang magtanong ukol sa Form 137, Good Moral Certificate, proseso ng pick-up sa campus, o tracking ng iyong request. Halimbawa: 'Paano kunin ang Good Moral?' o 'Saan kukunin ang docs?'"
            : "You can ask me about Form 137, Good Moral Certificates, Report Cards, campus claiming procedures, or request statuses. For example: 'Where do I claim my documents?' or 'What is Form 137 for?'";
    }

    private boolean isTagalogOrTaglish(String text) {
        String[] words = text.split("\\s+");
        for (String w : words) {
            if (filipinoKeywords.contains(w)) return true;
        }
        return false;
    }
}