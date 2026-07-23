package requestresult;

public record RegisterResult(int resultCode, RegAuthReturn returnAuth, String message) {
}
