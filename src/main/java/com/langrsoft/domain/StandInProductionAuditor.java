package com.langrsoft.domain;

// START:impl
public class StandInProductionAuditor implements Auditor {
   @Override
   public void audit(String message) {
      throw new RuntimeException("not yet implemented");
   }
}
// END:impl
