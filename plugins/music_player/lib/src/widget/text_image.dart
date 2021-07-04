import 'dart:convert';
import 'dart:math';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:crypto/crypto.dart';

import '../ext/canvas_ext.dart';

const colors = [Color(0xFFFF9100), Color(0xFFFF1744), Color(0xFF2196F3)];

class TextImage extends StatefulWidget {
  final String text;

  const TextImage({
    Key? key,
    required this.text,
  }) : super(key: key);

  @override
  State<TextImage> createState() => _TextImageState();
}

class _TextImageState extends State<TextImage> {
  late Color color;

  @override
  void initState() {
    super.initState();
    color = colors[md5.convert(utf8.encode(widget.text)).bytes[12] % 3];
  }

  @override
  Widget build(BuildContext context) {
    return ClipRect(
      child: CustomPaint(
        painter: _TextPainter(
          color: color,
          text: widget.text,
        ),
      ),
    );
  }
}

class _TextPainter extends CustomPainter {
  final Color color;
  final String text;

  _TextPainter({
    required this.color,
    required this.text,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final center = size.center(Offset.zero);
    canvas.drawRect(Offset.zero & size, Paint()..color = color);
    for (int i = 0; i < text.length; i++) {
      if (i >= 3) {
        break;
      }
      canvas.translate(center.dx, center.dy);
      canvas.rotate(pi / 4 * i);
      canvas.translate(-center.dx, -center.dy);
      canvas.drawText(
        text[i],
        size.center(Offset.zero),
        textAlign: TextAlign.center,
        textStyle: TextStyle(
          fontSize: size.width * i,
          color: Colors.white.withOpacity(0.3),
          fontWeight: FontWeight.bold,
        ),
      );
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return true;
  }
}
