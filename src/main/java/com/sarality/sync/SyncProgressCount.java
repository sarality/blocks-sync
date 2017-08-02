package com.sarality.sync;

/**
 * report sync executor page counts to sync service
 *
 * @author satya (satya puniani)
 */

public class SyncProgressCount {
  private int currentPage;
  private int maxPages;
  private int currentPageItem;
  private int maxItemsOnPage;

  SyncProgressCount(int currentPage, int maxPages, int currentPageItem, int maxItemsOnPage) {
    this.currentPage = currentPage;
    this.maxItemsOnPage = maxItemsOnPage;
    this.maxPages = maxPages;
    this.currentPageItem = currentPageItem;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public int getMaxPages() {
    return maxPages;
  }

  public int getCurrentPageItem() {
    return currentPageItem;
  }

  public int getMaxItemsOnPage() {
    return maxItemsOnPage;
  }

}
