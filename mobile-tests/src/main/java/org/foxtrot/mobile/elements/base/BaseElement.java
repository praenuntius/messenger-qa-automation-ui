package org.foxtrot.mobile.elements.base;

import com.codeborne.selenide.*;
import com.codeborne.selenide.ex.UIAssertionError;
import io.appium.java_client.AppiumBy;
import org.foxtrot.mobile.config.MobilePlatformResolver;
import org.foxtrot.mobile.core.application.MobileAppInfo;
import org.foxtrot.mobile.core.driver.DriverManager;
import org.foxtrot.mobile.elements.actions.Direction;
import org.foxtrot.mobile.elements.actions.SwipeHelper;
import org.foxtrot.mobile.locators.LocatorProcessor;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static org.foxtrot.mobile.config.MobilePlatformResolver.isAndroid;
import static org.foxtrot.mobile.config.MobilePlatformResolver.isIos;

public abstract class BaseElement {

    private static final int STALE_RETRIES = 3;
    private static final String ELEMENT_NOT_FOUND = "Element not found";

    protected final BaseElement parent;
    protected final By locator;
    protected final Integer index;

    protected BaseElement(final By locator) {
        this(null, locator, (Integer) null);
    }

    protected BaseElement(final By locator, final Integer index) {
        this(null, locator, index);
    }

    protected BaseElement(final By androidLocator, final By iosLocator) {
        this(null, LocatorProcessor.getLocator(androidLocator, iosLocator), (Integer) null);
    }

    protected BaseElement(final By androidLocator, final Integer androidIndex, final By iosLocator,
            final Integer iosIndex) {
        this(null, LocatorProcessor.getLocator(androidLocator, iosLocator),
                LocatorProcessor.getIndex(androidIndex, iosIndex));
    }

    protected BaseElement(final BaseElement parent, final By locator) {
        this(parent, locator, (Integer) null);
    }

    protected BaseElement(final BaseElement parent, final By locator, final Integer index) {
        this.parent = parent;
        this.locator = Objects.requireNonNull(locator, "Locator не может быть null");
        this.index = index;
    }

    protected BaseElement(final BaseElement parent, final By androidLocator, final By iosLocator) {
        this(parent, LocatorProcessor.getLocator(androidLocator, iosLocator), (Integer) null);
    }

    protected BaseElement(final BaseElement parent, final By androidLocator, final Integer androidIndex,
            final By iosLocator, final Integer iosIndex) {
        this(parent, LocatorProcessor.getLocator(androidLocator, iosLocator),
                LocatorProcessor.getIndex(androidIndex, iosIndex));
    }

    protected SelenideElement element() {
        try {
            ElementsCollection allElements = findAllElements();
            ElementsCollection visibleElements = allElements.filter(Condition.visible);
            if (index != null) {
                if (visibleElements.size() <= index && isIos() && !allElements.isEmpty()) {
                    scrollIosIntoView(allElements.get(Math.min(index, allElements.size() - 1)));
                    allElements = findAllElements();
                    visibleElements = allElements.filter(Condition.visible);
                }
                if (visibleElements.size() > index) {
                    return visibleElements.get(index);
                }
                if (isIos()) {
                    List<SelenideElement> onScreen = onScreenElements(allElements);
                    if (onScreen.size() > index) {
                        return onScreen.get(index);
                    }
                    if (allElements.size() > index) {
                        scrollIosIntoView(allElements.get(index));
                        return allElements.get(index);
                    }
                }
                visibleElements.shouldHave(CollectionCondition.sizeGreaterThan(index));
                return visibleElements.get(index);
            }
            if (!visibleElements.isEmpty()) {
                return visibleElements.first();
            }
            if (isIos() && !allElements.isEmpty()) {
                scrollIosIntoView(allElements.first());
                allElements = findAllElements();
                visibleElements = allElements.filter(Condition.visible);
                if (!visibleElements.isEmpty()) {
                    return visibleElements.first();
                }
                SelenideElement onScreen = firstOnScreen(allElements);
                if (onScreen != null) {
                    return onScreen;
                }
            }
            allElements.shouldBe(CollectionCondition.sizeGreaterThan(0));
            visibleElements = allElements.filter(Condition.visible);
            if (!visibleElements.isEmpty()) {
                return visibleElements.first();
            }
            if (isIos()) {
                SelenideElement onScreen = firstOnScreen(allElements);
                if (onScreen != null) {
                    return onScreen;
                }
            }
            visibleElements.shouldHave(CollectionCondition.sizeGreaterThan(0));
            return visibleElements.first();
        } catch (UIAssertionError e) {
            throw new NoSuchElementException(ELEMENT_NOT_FOUND + ", locator: " + locator
                    + (index != null ? ", index: " + index : "") + ". " + e.getMessage(), e);
        }
    }

    private ElementsCollection findAllElements() {
        return parent == null ? Selenide.$$(locator) : parent.element().$$(locator);
    }

    private ElementsCollection findAll() {
        return findAllElements();
    }

    public <T extends BaseElement> List<T> findElements(By locator, LazyElementCreator<T> creator) {
        try {
            ElementsCollection visibleElements = (parent == null
                    ? Selenide.$$(locator)
                    : parent.element().$$(locator))
                    .filter(Condition.visible)
                    .shouldBe(CollectionCondition.sizeNotEqual(0));
            int size = visibleElements.size();
            List<T> result = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                result.add(creator.create(locator, i));
            }
            return result;
        } catch (UIAssertionError e) {
            throw new NoSuchElementException(ELEMENT_NOT_FOUND + ", locator: " + locator + ". " + e.getMessage(), e);
        }
    }

    /**
     * iOS XCUI часто ставит {@code visible=false} ячейкам, которые реально на экране
     * (UITableView / оверлей). Selenide {@code Condition.visible} тогда пустой.
     */
    private SelenideElement readyForInteraction() {
        SelenideElement el = element();
        if (isIos()) {
            return el;
        }
        return el.shouldBe(Condition.visible).shouldBe(Condition.enabled);
    }

    // Действия с элементом
    protected void click() {
        withStaleRetry(() -> readyForInteraction().click());
    }

    /**
     * Короткий W3C-тап. Selenide/WDA click по iOS StaticText в ChatList часто
     * воспринимается как long-press и открывает ActionsMenu вместо чата.
     * Бьём в левую треть элемента (аватар ячейки), не в title.
     */
    protected void shortTap() {
        withStaleRetry(() -> {
            SelenideElement selenideElement = readyForInteraction();
            Rectangle rect = selenideElement.toWebElement().getRect();
            int x = rect.getX() + Math.min(16, Math.max(8, rect.getWidth() / 8));
            int y = rect.getY() + rect.getHeight() / 2;
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence sequence = new Sequence(finger, 1);
            sequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
            sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(new Pause(finger, Duration.ofMillis(30)));
            sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            DriverManager.getDriver().perform(List.of(sequence));
        });
    }

    protected void doubleTapAndLongPress() {
        withStaleRetry(() -> {
            SelenideElement selenideElement = readyForInteraction();
            Rectangle rect = selenideElement.toWebElement().getRect();
            int x = rect.getX() + rect.getWidth() / 2;
            int y = rect.getY() + rect.getHeight() / 2;
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence sequence = new Sequence(finger, 1);
            sequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
            sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(new Pause(finger, Duration.ofMillis(80)));
            sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(new Pause(finger, Duration.ofMillis(120)));
            sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(new Pause(finger, Duration.ofMillis(80)));
            sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(new Pause(finger, Duration.ofMillis(120)));
            sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(new Pause(finger, Duration.ofMillis(1000)));
            sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            DriverManager.getDriver().perform(List.of(sequence));
        });
    }

    protected void sendKeys(String text) {
        withStaleRetry(() -> {
            SelenideElement el = readyForInteraction();
            el.click();
            el.sendKeys(text);
        });
    }

    protected void clear() {
        withStaleRetry(() -> {
            SelenideElement el = readyForInteraction();
            el.click();
            el.clear();
        });
    }

    protected boolean isEnabled() {
        return withStaleRetry(() -> element().is(Condition.enabled));
    }

    protected boolean isChecked() {
        return withStaleRetry(() -> element().is(Condition.attribute("checked", "true")));
    }

    protected String getText() {
        return withStaleRetry(() -> readText(readyForInteraction()));
    }

    protected String getText(long seconds) {
        return withStaleRetry(() -> {
            if (!waitUntilPresent(seconds)) {
                findAllElements()
                        .filter(Condition.visible)
                        .shouldBe(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(seconds));
            }
            return readText(readyForInteraction());
        });
    }

    protected boolean isDisplayed() {
        try {
            return withStaleRetry(() -> hasVisibleMatch(true));
        } catch (UIAssertionError | WebDriverException ignored) {
            return false;
        }
    }

    protected boolean isDisplayed(long seconds) {
        try {
            return withStaleRetry(() -> waitUntilPresent(seconds));
        } catch (UIAssertionError | WebDriverException ignored) {
            return false;
        }
    }

    private boolean waitUntilPresent(long seconds) {
        long deadline = System.currentTimeMillis() + Math.max(0L, seconds) * 1000L;
        do {
            if (hasVisibleMatch(true)) {
                return true;
            }
            try {
                Thread.sleep(250L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        } while (System.currentTimeMillis() < deadline);
        return hasVisibleMatch(true);
    }

    private boolean hasVisibleMatch(boolean scrollHiddenIos) {
        ElementsCollection all = findAllElements();
        if (!all.filter(Condition.visible).isEmpty()) {
            return true;
        }
        if (isIos() && firstOnScreen(all) != null) {
            return true;
        }
        if (scrollHiddenIos && isIos() && !all.isEmpty()) {
            scrollIosIntoView(all.first());
            ElementsCollection refreshed = findAllElements();
            return !refreshed.filter(Condition.visible).isEmpty() || firstOnScreen(refreshed) != null;
        }
        return false;
    }

    private static String readText(SelenideElement el) {
        String text = el.getText();
        if (text != null && !text.isBlank()) {
            return text;
        }
        if (isIos()) {
            String label = el.getAttribute("label");
            if (label != null && !label.isBlank() && !"null".equalsIgnoreCase(label)) {
                return label;
            }
            String value = el.getAttribute("value");
            if (value != null && !value.isBlank() && !"null".equalsIgnoreCase(value)) {
                return value;
            }
        }
        return text == null ? "" : text;
    }

    private static SelenideElement firstOnScreen(ElementsCollection all) {
        List<SelenideElement> onScreen = onScreenElements(all);
        return onScreen.isEmpty() ? null : onScreen.get(0);
    }

    private static List<SelenideElement> onScreenElements(ElementsCollection all) {
        List<SelenideElement> result = new ArrayList<>();
        if (all == null || all.isEmpty()) {
            return result;
        }
        Dimension size;
        try {
            size = DriverManager.getDriver().manage().window().getSize();
        } catch (RuntimeException e) {
            return result;
        }
        int count = Math.min(all.size(), 40);
        for (int i = 0; i < count; i++) {
            SelenideElement el = all.get(i);
            if (isOnScreen(el, size)) {
                result.add(el);
            }
        }
        return result;
    }

    /** Пересечение с viewport — off-screen страница пейджера (x=422 при ширине 402) не матчится. */
    private static boolean isOnScreen(SelenideElement el, Dimension size) {
        try {
            Rectangle rect = el.getRect();
            if (rect.getWidth() <= 1 || rect.getHeight() <= 1) {
                return false;
            }
            int right = rect.getX() + rect.getWidth();
            int bottom = rect.getY() + rect.getHeight();
            return right > 8 && rect.getX() < size.getWidth() - 8
                    && bottom > 8 && rect.getY() < size.getHeight() - 8;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /** iOS UITableView держит off-screen ячейки в дереве с visible=false. */
    private static void scrollIosIntoView(SelenideElement el) {
        try {
            DriverManager.getDriver().executeScript("mobile: scroll",
                    Map.of("element", el.getWrappedElement(), "toVisible", true));
        } catch (RuntimeException ignored) {
            // контейнер может быть не скроллируемым
        }
    }

    public boolean isPlaceholder() {
        return withStaleRetry(() -> {
            SelenideElement el = readyForInteraction();
            String value = isAndroid() ? el.getAttribute("content-desc") : el.getAttribute("label");
            return value != null && !value.isBlank() && !"null".equalsIgnoreCase(value);
        });
    }

    protected void swipe(Direction direction) {
        withStaleRetry(() -> SwipeHelper.swipeFromElement(element(), direction));
    }

    protected void swipe(Direction direction, double percent, Duration duration) {
        withStaleRetry(() -> SwipeHelper.swipeFromElement(element(), direction, percent, duration));
    }

    private void withStaleRetry(Runnable action) {
        withStaleRetry(() -> {
            action.run();
            return null;
        });
    }

    private <T> T withStaleRetry(Supplier<T> action) {
        for (int attempt = 1; ; attempt++) {
            try {
                return action.get();
            } catch (NoSuchElementException e) {
                throw e;
            } catch (WebDriverException | UIAssertionError e) {
                if (isStaleRelated(e) && attempt < STALE_RETRIES) {
                    continue;
                }
                String reason = isStaleRelated(e)
                        ? "stale after " + attempt + " attempts"
                        : "interaction failed";
                throw new NoSuchElementException(ELEMENT_NOT_FOUND + " (" + reason + "), locator: " + locator
                        + (index != null ? ", index: " + index : "")
                        + ". " + e.getMessage(), e);
            }
        }
    }

    private static boolean isStaleRelated(Throwable error) {
        for (Throwable t = error; t != null; t = t.getCause()) {
            if (t instanceof StaleElementReferenceException) {
                return true;
            }
            String message = t.getMessage();
            if (message != null && (message.contains("stale element") || message.contains("Cached elements")
                    || message.contains("do not exist in DOM"))) {
                return true;
            }
        }
        return false;
    }

    // Стратегии поиска
    public static By byResourceId(String resourceId) {
        return AppiumBy.id(resolveResourceId(resourceId));
    }

    public static By byExternalResourceId(String fullResourceId) {
        return AppiumBy.id(fullResourceId);
    }

    public static By byAccessibilityId(String accessibilityId) {
        return AppiumBy.accessibilityId(accessibilityId);
    }

    public static By byClassName(String className) {
        return AppiumBy.className(className);
    }

    public static By byXpath(String xpath) {
        return By.xpath(xpath);
    }

    /**
     * Имя N-й Compose-ячейки в списке. Первая TextView — имя,
     * вторая может быть подписью («Бот», «Владелец») и не является отдельным контактом.
     */
    public static By byComposeCellTitle(String listResourceId, int oneBasedIndex) {
        return byXpath("(//*[@resource-id='%s']//androidx.compose.ui.platform.ComposeView)[%d]//android.widget.TextView[not(@resource-id)][1]"
                .formatted(resolveResourceId(listResourceId), oneBasedIndex));
    }

    /**
     * RC Compose-ячейки контактов: без resource-id. Заголовок секции {@code title} исключаем.
     * В имени иногда двойной пробел — для поиска по тексту используем normalize-space.
     */
    public static By byContactsListText(int oneBasedIndex) {
        return byComposeCellTitle("contacts", oneBasedIndex);
    }

    public static By byContactsListText(String text) {
        return byXpath(".//android.widget.TextView[not(@resource-id) and normalize-space(@text)='%s']"
                .formatted(text));
    }

    public static By byListItemName(String text) {
        return byXpath(".//android.widget.TextView[normalize-space(@text)='%s']".formatted(text));
    }

    public static By byEqualsText(String text) {
        if (isAndroid()) {
            return AppiumBy.androidUIAutomator("new UiSelector().text(\"%s\")".formatted(text));
        }
        if (MobilePlatformResolver.isIos()) {
            return AppiumBy.iOSNsPredicateString("name == '%s' OR label == '%s' OR value == '%s'"
                    .formatted(text, text, text));
        }
        throw new RuntimeException("Платформа не поддерживается: " + MobilePlatformResolver.getPlatform());
    }

    public static By byContainsText(String text) {
        if (isAndroid()) {
            return AppiumBy.androidUIAutomator("new UiSelector().textContains(\"%s\")".formatted(text));
        }
        if (MobilePlatformResolver.isIos()) {
            return AppiumBy.iOSNsPredicateString("name CONTAINS '%s' OR label CONTAINS '%s' OR value CONTAINS '%s'"
                    .formatted(text, text, text));
        }
        throw new RuntimeException("Платформа не поддерживается: " + MobilePlatformResolver.getPlatform());
    }

    public static By byResourceIdAndText(String resourceId, String text) {
        return AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"%s\").text(\"%s\")"
                .formatted(resolveResourceId(resourceId), text)
        );
    }

    public static By byExternalResourceIdAndText(String fullResourceId, String text) {
        return AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"%s\").text(\"%s\")"
                .formatted(fullResourceId, text)
        );
    }

    public static By byResourceIdAndContainsText(String resourceId, String text) {
        return AppiumBy.androidUIAutomator("new UiSelector().resourceId(\"%s\").textContains(\"%s\")"
                .formatted(resolveResourceId(resourceId), text)
        );
    }

    public static By byAccessibilityIdAndText(String accessibilityId, String text) {
        return AppiumBy.iOSNsPredicateString("name == '%s' AND (label == '%s' OR value == '%s')"
                .formatted(accessibilityId, text, text)
        );
    }

    public static By byAccessibilityIdAndContainsText(String accessibilityId, String text) {
        return AppiumBy.iOSNsPredicateString("name == '%s' AND (label CONTAINS '%s' OR value CONTAINS '%s')"
                .formatted(accessibilityId, text, text)
        );
    }

    private static String resolveResourceId(String resourceId) {
        return isAndroid() ? MobileAppInfo.androidResourceId(resourceId) : resourceId;
    }
}