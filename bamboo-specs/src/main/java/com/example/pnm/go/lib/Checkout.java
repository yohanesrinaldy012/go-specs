package com.example.pnm.go.lib;

import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;

public final class Checkout {
  private Checkout() {}

  public static VcsCheckoutTask defaultRepo() {
    return new VcsCheckoutTask()
        .description("Checkout default repo (branch BRANCH_NAME if set)")
        .checkoutItems(new CheckoutItem().defaultRepository());
  }
}
