import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:palette_generator/palette_generator.dart';

typedef PaletteGeneratorBuilder = Widget Function(
    BuildContext context, PaletteGenerator? generator, Widget? child);

/// 图片调色盘
class PaletteBuilder extends StatefulWidget {
  /// 子组件
  final Widget? child;

  /// 构造器
  final PaletteGeneratorBuilder builder;

  /// 图片提供器
  final ImageProvider imageProvider;

  const PaletteBuilder(
    this.imageProvider, {
    Key? key,
    required this.builder,
    this.child,
  }) : super(key: key);

  @override
  _PaletteBuilderState createState() => _PaletteBuilderState();
}

class _PaletteBuilderState extends State<PaletteBuilder> {
  PaletteGenerator? _paletteGenerator;

  Future<void> _refreshPalette() async {
    try {
      _paletteGenerator =
          await PaletteGenerator.fromImageProvider(widget.imageProvider);
      if (mounted) {
        setState(() {});
      }
    } catch (e) {}
  }

  @override
  void initState() {
    super.initState();
    _refreshPalette();
  }

  @override
  void dispose() {
    widget.imageProvider.evict();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return widget.builder(context, _paletteGenerator, widget.child);
  }
}
