package com.br.mytasksapp.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class Mask {

    public enum MaskType {
        CPF,
        CNPJ, PHONE, HOUR, CEP, BIRTHDAY, VALUE, VALID_CARD, NUMBER_CARD
    }

    private static final String CNPJMask = "##.###.###/####-##";
    private static final String CPFMask = "###.###.###-##";
    private static final String TelMask = "(##) #####-####";
    private static final String HoraMask = "##:##";
    private static final String CEPMask = "#####-###";
    private static final String BIRTHDAYMask = "##/##/####";
    private static final String ValueMask = "###,##";
    private static final String validCard = "##/##";
    private static final String numberCard = "#### #### #### ####";

    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "").replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]","").replaceAll("[:]", "").replaceAll("[)]", "");
    }

    private static String getDefaultMask(String str) {
        String defaultMask = CPFMask;
        if (str.length() == 14) {
            defaultMask = CNPJMask;
        } else if (str.length() == 8) {
            defaultMask = CEPMask;
        } else if (str.length() == 10) {
            defaultMask = TelMask;
        } else if (str.length() == 4) {
            defaultMask = HoraMask;
        }
        return CPFMask;
    }

    public static TextWatcher insert(final EditText editText, final MaskType maskType) {
        return new TextWatcher() {

            boolean isUpdating;
            String oldValue = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = Mask.unmask(s.toString());
                String mask;
                switch (maskType) {
                    case CPF:
                        mask = CPFMask;
                        break;
                    case CNPJ:
                        mask = CNPJMask;
                        break;
                    case PHONE:
                        mask = TelMask;
                        break;
                    case CEP:
                        mask = CEPMask;
                        break;
                    case HOUR:
                        mask = HoraMask;
                        break;
                    case BIRTHDAY:
                        mask = BIRTHDAYMask;
                        break;
                    case VALUE:
                        mask = ValueMask;
                        break;
                    case VALID_CARD:
                        mask = validCard;
                        break;
                    case NUMBER_CARD:
                        mask = numberCard;
                        break;
                    default:
                        mask = getDefaultMask(value);
                        break;
                }
                String maskAux = "";
                if(isUpdating) {
                    oldValue = value;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if((m != '#' && value.length() > oldValue.length()) || (m != '#' && value.length() < oldValue.length() && value.length() != i)) {
                        maskAux += m;
                        continue;
                    }
                    try {
                        maskAux += value.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(maskAux);
                editText.setSelection(maskAux.length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}