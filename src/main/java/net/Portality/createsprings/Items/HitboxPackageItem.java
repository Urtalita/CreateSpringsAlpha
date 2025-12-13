package net.Portality.createsprings.Items;

import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;

public class HitboxPackageItem extends PackageItem {
    public HitboxPackageItem(Properties properties, PackageStyles.PackageStyle style) {
        super(properties, style);
        PackageStyles.STANDARD_BOXES.remove(this);
        PackageStyles.ALL_BOXES.remove(this);
    }
}
