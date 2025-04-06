package com.careerconnect.util;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.boot.ansi.AnsiBackground;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

public class MessageHighlightConverter extends ClassicConverter {

    // Định nghĩa mã màu bạn muốn cho message
    // Ở đây là chữ đen (BLACK) trên nền vàng nhạt (BRIGHT_YELLOW)
    // Bạn có thể thay đổi AnsiColor và AnsiBackground theo ý muốn
//    private static final String START_CODE = AnsiOutput.encode(AnsiBackground.BRIGHT_YELLOW) + AnsiOutput.encode(AnsiColor.BLACK);
    private static final String START_CODE = "\u001B[48;5;226m";
    private static final String END_CODE = AnsiOutput.encode(AnsiColor.DEFAULT) + AnsiOutput.encode(AnsiBackground.DEFAULT); // Reset cả màu chữ và nền

    @Override
    public String convert(ILoggingEvent event) {
        // Lấy nội dung message gốc
        String message = event.getFormattedMessage();
//        nếu ko phải info
        if (!event.getLevel().toString().equals("ERROR")) {
            return message;
        }
        // Bọc message bằng mã màu bắt đầu và kết thúc (reset)
        return START_CODE + message + END_CODE;
    }
}