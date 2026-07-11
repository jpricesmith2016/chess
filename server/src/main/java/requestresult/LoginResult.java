package requestresult;

public record LoginResult(int resultCode, String message, String authToken) {
}
