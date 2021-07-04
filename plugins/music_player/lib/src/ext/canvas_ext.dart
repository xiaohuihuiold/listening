import 'package:flutter/material.dart';

extension CanvasExt on Canvas {
  void drawText(
    String text,
    Offset position, {
    TextStyle textStyle =
        const TextStyle(color: Colors.black87, fontSize: 12.0),
    TextAlign textAlign = TextAlign.start,
  }) {
    final TextPainter textPainter = TextPainter(
      text: TextSpan(
        text: text,
        style: textStyle,
      ),
      textDirection: TextDirection.ltr,
    );
    textPainter.layout();
    textPainter.paint(this, () {
      switch (textAlign) {
        case TextAlign.center:
          return position - (textPainter.size / 2.0).bottomRight(Offset.zero);
        case TextAlign.start:
          return position - Offset(textPainter.size.width / 2.0, 0.0);
        case TextAlign.left:
          return position - Offset(0.0, textPainter.size.height / 2.0);
        default:
          return position;
      }
    }());
  }
}
