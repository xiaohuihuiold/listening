import 'package:flutter/material.dart';
import 'package:listening/page/home/album_list.dart';
import 'package:listening/page/home/home_provider.dart';
import 'package:listening/page/home/play_view.dart';
import 'package:listening/widget/music_bar.dart';
import 'package:music_player/music_player.dart';

/// 主页
class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> with TickerProviderStateMixin {
  late TabController _tabController;
  final PageController _pageController = PageController();
  int _selectedIndex = 0;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    HomeProvider.read(context).init();
  }

  @override
  Widget build(BuildContext context) {
    final themeData = Theme.of(context);
    final body = LayoutBuilder(
      builder: (_, constraints) {
        final size = constraints.biggest;
        final isHorizontal = size.width > size.height;
        return Row(
          children: [
            if (isHorizontal)
              NavigationRail(
                elevation: 2.0,
                selectedIndex: _selectedIndex,
                backgroundColor: themeData.colorScheme.surface.withOpacity(0.8),
                onDestinationSelected: (index) {
                  setState(() {
                    _selectedIndex = index;
                  });
                  _tabController.index = index;
                  _pageController.animateToPage(
                    index,
                    duration: const Duration(milliseconds: 400),
                    curve: Curves.fastOutSlowIn,
                  );
                },
                leading: FloatingActionButton(
                  elevation: 0,
                  backgroundColor: Theme.of(context).primaryColor,
                  child: const FlutterLogo(size: 38),
                  onPressed: () {
                    MusicPlayer.play(schema: MusicSchema.allMusic);
                  },
                ),
                destinations: const [
                  NavigationRailDestination(
                    icon: Icon(Icons.home_outlined),
                    selectedIcon: Icon(Icons.home),
                    label: Text('首页'),
                  ),
                  NavigationRailDestination(
                    icon: Icon(Icons.album_outlined),
                    selectedIcon: Icon(Icons.album),
                    label: Text('专辑'),
                  ),
                  NavigationRailDestination(
                    icon: Icon(Icons.favorite_outline),
                    selectedIcon: Icon(Icons.favorite),
                    label: Text('喜欢'),
                  ),
                ],
              ),
            Expanded(
              child: Column(
                children: [
                  if (!isHorizontal)
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8.0),
                      margin: const EdgeInsets.only(bottom: 8.0),
                      alignment: Alignment.centerLeft,
                      child: TabBar(
                        onTap: (index) {
                          setState(() {
                            _selectedIndex = index;
                          });
                          _pageController.animateToPage(
                            index,
                            duration: const Duration(milliseconds: 400),
                            curve: Curves.fastOutSlowIn,
                          );
                        },
                        controller: _tabController,
                        isScrollable: true,
                        indicatorWeight: 5,
                        indicatorSize: TabBarIndicatorSize.label,
                        labelColor: themeData.colorScheme.secondary,
                        labelStyle: const TextStyle(fontSize: 34.0),
                        unselectedLabelColor: Colors.grey,
                        unselectedLabelStyle: const TextStyle(fontSize: 22.0),
                        tabs: const [
                          Tab(text: '首页'),
                          Tab(text: '专辑'),
                          Tab(text: '喜欢'),
                        ],
                      ),
                    ),
                  Expanded(
                    child: PageView(
                      controller: _pageController,
                      scrollDirection:
                          isHorizontal ? Axis.vertical : Axis.horizontal,
                      physics: isHorizontal
                          ? const NeverScrollableScrollPhysics()
                          : const BouncingScrollPhysics(),
                      onPageChanged: (index) {
                        setState(() {
                          _selectedIndex = index;
                        });
                        _tabController.animateTo(index);
                      },
                      children: [
                        PlayView(isHorizontal: isHorizontal),
                        const AlbumList(),
                        const Center(child: Text('喜欢')),
                      ],
                    ),
                  ),
                  if ((_selectedIndex != 0 && isHorizontal) || !isHorizontal)
                    const MusicBar(),
                ],
              ),
            ),
          ],
        );
      },
    );
    return MusicBackground(
      child: Scaffold(
        body: SafeArea(child: body),
      ),
    );
  }
}
