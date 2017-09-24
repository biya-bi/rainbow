package org.rainbow.security.auth.faces.controllers;

import static org.rainbow.security.auth.faces.utilities.ResourceBundles.MESSAGES;
import static org.rainbow.security.auth.faces.utilities.ResourceBundles.RECOVERY_QUESTIONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.inject.Named;

import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.security.service.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@Named
@SessionScope
public class ResetRecoveryInformationController extends AbstractUserNameController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -924279992879597189L;

	private static final String FAILURE = "failure";
	private static final String SUCCESS = "success";

	private static final String RESET_RECOVERY_INFORMATION_FAILED_KEY = "ResetRecoveryInformationFailed";

	@Autowired
	@Qualifier("userAccountService")
	private UserAccountService userAccountService;

	@Autowired
	private AuthenticationExceptionHandler authenticationExceptionHandler;

	private String password;
	private String question1;
	private String answer1;
	private String question2;
	private String answer2;
	private String question3;
	private String answer3;
	private String question4;
	private String answer4;
	private Set<Entry<String, String>> questions;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQuestion1() {
		return question1;
	}

	public void setQuestion1(String question1) {
		this.question1 = question1;
	}

	public String getAnswer1() {
		return answer1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	public String getQuestion2() {
		return question2;
	}

	public void setQuestion2(String question2) {
		this.question2 = question2;
	}

	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	public String getQuestion3() {
		return question3;
	}

	public void setQuestion3(String question3) {
		this.question3 = question3;
	}

	public String getAnswer3() {
		return answer3;
	}

	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}

	public String getQuestion4() {
		return question4;
	}

	public void setQuestion4(String question4) {
		this.question4 = question4;
	}

	public String getAnswer4() {
		return answer4;
	}

	public void setAnswer4(String answer4) {
		this.answer4 = answer4;
	}

	public String resetRecoveryInformation() {
		try {
			// final List<String> qtns = Arrays.asList(this.getQuestion1(),
			// this.getQuestion2(), this.getQuestion3(),
			// this.getQuestion4());
			// if (!qtns.isEmpty() || qtns.stream().distinct().count() <
			// qtns.size()) {
			// throw new RecoveryQuestionsNotDistinctException();
			// }
			final Map<String, String> questionAnswerPairs = new HashMap<String, String>();
			questionAnswerPairs.put(question1, answer1);
			questionAnswerPairs.put(question2, answer2);
			questionAnswerPairs.put(question3, answer3);
			questionAnswerPairs.put(question4, answer4);

			userAccountService.resetRecoveryQuestions(this.getUserName(), this.getPassword(), questionAnswerPairs);
			return SUCCESS;
		} catch (Exception e) {
			FacesContextUtil.addMessage(FacesMessage.SEVERITY_WARN,
					authenticationExceptionHandler.handle(this.getClass(), e),
					ResourceBundle.getBundle(MESSAGES).getString(RESET_RECOVERY_INFORMATION_FAILED_KEY));
			return FAILURE;
		}
	}

	public Set<Entry<String, String>> getQuestions() {
		if (this.questions == null) {
			final HashMap<String, String> questionsMap = new HashMap<>();
			final ResourceBundle bundle = ResourceBundle.getBundle(RECOVERY_QUESTIONS);
			final Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				if (key != null && !key.isEmpty()) {
					questionsMap.put(key, bundle.getString(key));
				}
			}
			this.questions = sort(questionsMap).entrySet();
		}
		return questions;
	}

	private LinkedHashMap<String, String> sort(final HashMap<String, String> map) {
		final Comparator<Entry<String, String>> comparator = new Comparator<Entry<String, String>>() {
			@Override
			public int compare(Entry<String, String> e1, Entry<String, String> e2) {
				String v1 = e1.getValue();
				String v2 = e2.getValue();
				return v1.compareTo(v2);
			}
		};

		final Set<Entry<String, String>> set = map.entrySet();
		// Sort method needs a List, so let's first convert Set to List
		final List<Entry<String, String>> entries = new ArrayList<Entry<String, String>>(set);
		// Sorting HashMap by values using comparator
		Collections.sort(entries, comparator);
		final LinkedHashMap<String, String> sortedByValue = new LinkedHashMap<String, String>(entries.size());
		// Copying entries from List to Map
		for (Entry<String, String> entry : entries) {
			sortedByValue.put(entry.getKey(), entry.getValue());
		}
		return sortedByValue;
	}
}
