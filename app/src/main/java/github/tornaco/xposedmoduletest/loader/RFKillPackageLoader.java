package github.tornaco.xposedmoduletest.loader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import github.tornaco.xposedmoduletest.bean.RFKillPackage;
import github.tornaco.xposedmoduletest.util.PinyinComparator;
import github.tornaco.xposedmoduletest.xposed.app.XAshmanManager;
import github.tornaco.xposedmoduletest.xposed.util.PkgUtil;

/**
 * Created by guohao4 on 2017/10/18.
 * Email: Tornaco@163.com
 */

public interface RFKillPackageLoader {

    @NonNull
    List<RFKillPackage> loadInstalled(boolean blocked);

    class Impl implements RFKillPackageLoader {

        public static RFKillPackageLoader create(Context context) {
            return new Impl(context);
        }

        private Context context;

        private Impl(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public List<RFKillPackage> loadInstalled(boolean willBeKill) {

            List<RFKillPackage> out = new ArrayList<>();

            XAshmanManager xAshmanManager = XAshmanManager.singleInstance();
            if (!xAshmanManager.isServiceAvailable()) return out;

            String[] packages = xAshmanManager.getRFKApps(willBeKill);

            for (String pkg : packages) {
                String name = String.valueOf(PkgUtil.loadNameByPkgName(context, pkg));
                if (!TextUtils.isEmpty(name)) {
                    name = name.replace(" ", "");
                }

                RFKillPackage p = new RFKillPackage();
                p.setKill(true);
                p.setAppName(name);
                p.setPkgName(pkg);
                p.setSystemApp(PkgUtil.isSystemApp(context, pkg));

                out.add(p);
            }
            java.util.Collections.sort(out, new RFComparator());

            return out;
        }
    }

    class RFComparator implements Comparator<RFKillPackage> {
        public int compare(RFKillPackage o1, RFKillPackage o2) {
            return new PinyinComparator().compare(o1.getAppName(), o2.getAppName());
        }
    }
}