package forum.jiangyouluntan.com.videocropdemo.listVideo.visibility.calculator;

import android.view.View;

import forum.jiangyouluntan.com.videocropdemo.listVideo.visibility.items.ListItem;


/**
 * Default implementation. You can override it and intercept switching between active items
 *
 * @author Wayne
 */
public class DefaultSingleItemCalculatorCallback implements SingleListViewItemActiveCalculator.Callback<ListItem>{

    @Override
    public void activateNewCurrentItem(ListItem newListItem, View newView, int newViewPosition) {
        if (newListItem != null) {
            newListItem.setActive(newView, newViewPosition);
        }
    }

    @Override
    public void deactivateCurrentItem(ListItem listItemToDeactivate, View view, int position) {
        if (listItemToDeactivate != null) {
            listItemToDeactivate.deactivate(view, position);
        }
    }
}
