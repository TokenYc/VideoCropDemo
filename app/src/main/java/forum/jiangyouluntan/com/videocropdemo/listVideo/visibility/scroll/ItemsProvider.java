package forum.jiangyouluntan.com.videocropdemo.listVideo.visibility.scroll;


import forum.jiangyouluntan.com.videocropdemo.listVideo.visibility.items.ListItem;

/**
 * This interface is used by {@link com.wangjing.wedgit.listVideo.visibility.calculator.SingleListViewItemActiveCalculator}.
 * Using this class to get {@link com.wangjing.wedgit.listVideo.visibility.items.ListItem}
 *
 * @author Wayne
 */
public interface ItemsProvider {

    ListItem getListItem(int position);

    int listItemSize();

}
