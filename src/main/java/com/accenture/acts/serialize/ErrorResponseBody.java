package com.accenture.acts.serialize;

import java.util.List;

import com.accenture.acts.rest.classic.ErrorsField;
import com.accenture.acts.rest.classic.ErrorsField.ActsFieldError;
import com.accenture.acts.rest.classic.ErrorsField.ActsGlobalError;

/**
 * Errorが発生した場合のAPI Responseを整形するひな形。以下のような形式となる。
 *
 * <pre>
{
    "error": "string"
    "fields": [{
        "field": "string",
        "message": "string"
    }],
    "global": [{
        "message": "string"
    }]
}
 * </pre>
 *
 */
public class ErrorResponseBody {
    private String error;
    private List<ActsFieldError> fields;
    private List<ActsGlobalError> global;

    /**
     * コンストラクタ。
     *
     * @param errorStr errorフィールドに設定する文字列。
     * @param errors fields及びglobalエラーを保持するErrorsFieldオブジェクト
     */
    public ErrorResponseBody(String errorStr, ErrorsField errors) {
        super();
        this.error = errorStr;
        if (errors != null) {
            this.fields = errors.getFields();
            this.global = errors.getGlobal();
        }
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<ActsFieldError> getFields() {
        return fields;
    }

    public void setFields(List<ActsFieldError> fields) {
        this.fields = fields;
    }

    public List<ActsGlobalError> getGlobal() {
        return global;
    }

    public void setGlobal(List<ActsGlobalError> global) {
        this.global = global;
    }

}
