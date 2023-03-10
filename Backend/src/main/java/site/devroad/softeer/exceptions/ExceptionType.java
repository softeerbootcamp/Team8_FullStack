package site.devroad.softeer.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

@JsonFormat
public enum ExceptionType {
    //Account Related Exceptions
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Account not found", 1001),
    ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "Account already exists", 1002),
    POST_ACCOUNT_FORM_INVALID(HttpStatus.BAD_REQUEST, "null 값이 회원가입 요청에 들어있습니다.", 1003),
    POST_ACCOUNT_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 email이  있습니다.", 1004),
    ACCOUNT_NOT_UPDATED(HttpStatus.BAD_REQUEST, "Account not updated", 1005),
    AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "Password가 잘못되었습니다.", 1006),
    POST_ACCOUNT_PHONE_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 휴대폰 번호가 있습니다.", 1007),
    ROADMAP_NOT_FOUND(HttpStatus.NOT_FOUND, "Roadmap not found", 1008),
    SUBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "Subject not found", 1009),
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Course not found", 1010),

    CHAPTER_NOT_FOUND(HttpStatus.NOT_FOUND, "Chapter not found", 1011),
    SUBJECT_TO_ROADMAP_NOT_FOUND(HttpStatus.NOT_FOUND, "Subject to roadmap not found", 1012),



    //Database Related Exceptions
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Database error", 1101),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error", 1102),

    //JWT Related Exceptions
    JWT_NOT_VALID(HttpStatus.BAD_REQUEST, "JWT not valid", 1201),
    NO_ADMIN_USER(HttpStatus.BAD_REQUEST, "Not admin", 1202),



    //Exam Related Exceptions
    EXAM_NOT_FOUND(HttpStatus.NOT_FOUND, "Exam not exist", 1301),
    EXAM_NOT_PURCHASED(HttpStatus.BAD_REQUEST, "Exam not purchased", 1302),
    TOSS_ALREADY_PURCHASED(HttpStatus.BAD_REQUEST, "Toss purchase already done error",1303),
    TOSS_PURCHASE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Toss purchase io exception done error",1304),
    EXAM_ALREADY_PURCHASED(HttpStatus.BAD_REQUEST, "Exam Already purchased", 1304),
    EXAM_SUBMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Exam submission not exist", 1305),


    //Github erros
    GITHUB_API_ERROR_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "Github api 연동오류, apikey문제일 확율이 높습니다.", 1401 ),
    GITHUB_API_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Github api연동중 io 문제가 발생했습니다.", 1402 ),

    GITHUB_ISSUE_ALREADY_SENDED(HttpStatus.BAD_REQUEST, "Issue라는 글자가 이미 url에 들어있습니다.", 1403),
    //HTTP Connection Error
    CONNECTION_ESTABLISH_FAILED(HttpStatus.BAD_REQUEST, "Http connection error, 외부 연동에 문제가 존재합니다.", 1501),

    //AI Review Related Error
    AI_REVIEW_ALREADY_DONE(HttpStatus.BAD_REQUEST, "AI Review already done" , 1602),

    //Last Exceptions
    DATABASE_NOT_CONNECTED(HttpStatus.INTERNAL_SERVER_ERROR, "Database not connected", 2001);
    private HttpStatus httpStatus;
    private String message;
    private Integer customErrorCode;

    ExceptionType(HttpStatus httpStatus, String message, Integer customErrorCode) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.customErrorCode = customErrorCode;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    public Integer getCustomErrorCode() {
        return customErrorCode;
    }
}
