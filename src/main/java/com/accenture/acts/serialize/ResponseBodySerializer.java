package com.accenture.acts.serialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import com.accenture.acts.registry.ExceptionMetadataRegistry;
import com.accenture.acts.rest.ExceptionMetadata;
import com.accenture.acts.rest.ProcessResult;
import com.accenture.acts.rest.ResponseBodySerializerInterface;
import com.accenture.acts.rest.classic.ErrorsField;

/**
 * レスポンスボディSerialize用クラス。
 *
 * <p>
 * 正常時はControllerから返却されたオブジェクトをそのままJson形式でシリアライズしてクライアントに応答する。
 * </p>
 *
 * <p>
 * エラー時は{@link ErrorResponseBody}クラスのフィールドに従う形式で応答する。
 * </p>
 */
@JsonComponent
public class ResponseBodySerializer extends JsonSerializer<ProcessResult> implements ResponseBodySerializerInterface {

    private final ExceptionMetadataRegistry metadataRegistry;

    /**
     * レスポンスボディシリアライザ。
     *
     * @param metadataRegistry メタデータレジストリ
     */
    public ResponseBodySerializer(ExceptionMetadataRegistry metadataRegistry) {
        this.metadataRegistry = metadataRegistry;
    }

    @Override
    public void serialize(ProcessResult value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // 正常レスポンス
        if (!value.hasError()) {
            serializeSuccessBody(value, gen, provider);
        } else { // エラーレスポンス
            serializeErrorBody(value.getError(), gen);
        }

    }

    /**
     * エラーレスポンスをSerializeする。
     *
     * @param error 発生した例外
     * @param gen Json Generator
     * @throws IOException I/Oエラー
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void serializeErrorBody(Throwable error, JsonGenerator gen) throws IOException {
        ExceptionMetadata metadata = metadataRegistry.get(error);
        ErrorsField errorsField = metadata.getErrorsField(error);
        ErrorResponseBody body = new ErrorResponseBody(metadata.getErrorStatus(error), errorsField);
        gen.writeObject(body);
    }

    /**
     * 正常レスポンスをSerializeする。
     *
     * @param value レスポンス
     * @param gen Json Generator
     * @param provider Serialize Provider
     * @throws IOException I/Oエラー
     */
    private void serializeSuccessBody(ProcessResult value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
        if (value.getOutput() != null) {
            provider.defaultSerializeValue(value.getOutput(), gen);
        } else {
            gen.writeStartObject();
            gen.writeEndObject();
        }

    }
}
