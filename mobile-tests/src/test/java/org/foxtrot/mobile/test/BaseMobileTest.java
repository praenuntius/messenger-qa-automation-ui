package org.foxtrot.mobile.test;


import org.foxtrot.mobile.extensions.MobileTestLifecycleExtension;
import org.foxtrot.mobile.utils.MobilePages;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MobileTestLifecycleExtension.class)
public abstract class BaseMobileTest {

    protected final MobilePages pages = new MobilePages();
}
