package it.tredi.dw4.model.customfields;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Validator extends XmlEntity {
	
	public static final String VALIDATOR_TYPE_EMAIL = "email";
	public static final String VALIDATOR_TYPE_CF = "cf";
	public static final String VALIDATOR_TYPE_PIVA = "piva";
	public static final String VALIDATOR_TYPE_CF_OR_PIVA = "cf-piva";
	public static final String VALIDATOR_TYPE_DIGITS = "digits";
	public static final String VALIDATOR_TYPE_REGEX = "regex";
	
	// patter da utilizzare per validazione regex
	public static final String REGEX_PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final String REGEX_PATTERN_CF = "[a-zA-Z]{6}[0-9]{2}[a-zA-Z][0-9]{2}[a-zA-Z][0-9]{3}[a-zA-Z]";
	public static final String REGEX_PATTERN_PIVA = "[0-9]{11}";
	public static final String REGEX_PATTERN_CF_OR_PIVA = "(" + REGEX_PATTERN_CF + ")|(" + REGEX_PATTERN_PIVA + ")";
	public static final String REGEX_PATTERN_DIGITS = "^[0-9]*$";

	private String type = "";
	private String regex = "";
	private int minLength = 0;
	private int maxLength = 0;
	private String minValue = "";
	private String maxValue = "";

	private List<Attribute> allAttr = new ArrayList<>();
	
	@Override
	public XmlEntity init(Document dom) {
		this.type = XMLUtil.parseStrictAttribute(dom, "validator/@type");
		this.regex = XMLUtil.parseStrictAttribute(dom, "validator/@regex");
		String minStr = XMLUtil.parseStrictAttribute(dom, "validator/@min-length");
		if (minStr != null && !minStr.isEmpty())
			this.minLength = Integer.parseInt(minStr);
		String maxStr = XMLUtil.parseStrictAttribute(dom, "validator/@max-length");
		if (maxStr != null && !maxStr.isEmpty())
			this.maxLength = Integer.parseInt(maxStr);
		this.minValue = XMLUtil.parseStrictAttribute(dom, "validator/@min-value");
		this.minValue = evaluateValue(this.minValue);
		this.maxValue = XMLUtil.parseStrictAttribute(dom, "validator/@max-value");
		this.maxValue = evaluateValue(this.maxValue);

		if (dom != null) {
			Element validatorEl = (Element) dom.selectSingleNode("validator");
			if (validatorEl != null) {
				allAttr = validatorEl.attributes();
			}
		}

		return this;
	}
	
	private String evaluateValue(String value) {
		if (value != null) {
			if (value.equals("CURRENT_DATE")) {
				value = DateUtil.getLongDate(new Date());
			} else if (value.equals("CURRENT_YEAR")) {
				value = LocalDate.now().getYear()+"";
			}
		}
		return value;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getRegex() {
		return regex;
	}
	
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	public int getMinLength() {
		return minLength;
	}
	
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}
	
	public int getMaxLength() {
		return maxLength;
	}
	
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	
	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public List<Attribute> getAllAttr() {
		return allAttr;
	}

	/**
	 * Ritorna l'eventuale regola di validazione da applicare (regex) in base alla
	 * tipologia di validatore settata sul campo
	 * @return
	 */
	public String getValidationRule() {
		String regexRule = null;
		if (type != null) {
			if (type.equalsIgnoreCase(VALIDATOR_TYPE_EMAIL))
				regexRule = REGEX_PATTERN_EMAIL;
			else if (type.equalsIgnoreCase(VALIDATOR_TYPE_CF))
				regexRule = REGEX_PATTERN_CF;
			else if (type.equalsIgnoreCase(VALIDATOR_TYPE_PIVA))
				regexRule = REGEX_PATTERN_PIVA;
			else if (type.equalsIgnoreCase(VALIDATOR_TYPE_CF_OR_PIVA))
				regexRule = REGEX_PATTERN_CF_OR_PIVA;
			else if (type.equalsIgnoreCase(VALIDATOR_TYPE_DIGITS))
				regexRule = REGEX_PATTERN_DIGITS;
			else if (type.equalsIgnoreCase(VALIDATOR_TYPE_REGEX))
				regexRule = getRegex();
		}
		return regexRule;
	}
	
}
