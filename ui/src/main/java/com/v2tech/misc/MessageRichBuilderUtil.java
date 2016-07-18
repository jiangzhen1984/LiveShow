package com.v2tech.misc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.v2tech.R;
import com.v2tech.vo.msg.VMessage;
import com.v2tech.vo.msg.VMessageAbstractItem;
import com.v2tech.vo.msg.VMessageTextItem;

import java.util.List;

/**
 * Created by 28851274 on 7/18/16.
 */
public class MessageRichBuilderUtil {


    public static CharSequence buildContent(Context ctx, VMessage vm) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        List<VMessageAbstractItem> list = vm.getItems();
        for (VMessageAbstractItem item : list) {
            if (item.getType() == VMessageAbstractItem.ITEM_TYPE_TEXT) {
                builder.append(((VMessageTextItem) item).getText());
            } else if (item.getType() == VMessageAbstractItem.ITEM_TYPE_FACE) {
                SpannableStringBuilder emojiBuilder = new SpannableStringBuilder(
                        "[at]", 0, 4);
                Drawable dra = ctx.getResources().getDrawable(
                        R.drawable.emo_01);
                dra.setBounds(0, 0, dra.getIntrinsicWidth(),
                        dra.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(dra);
                emojiBuilder.setSpan(span, 0, 4,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(emojiBuilder);
            }
        }
        return builder;
    }
}
