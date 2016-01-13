package hudson.plugins.promoted_builds.dsl;

import hudson.plugins.promoted_builds.PromotionCondition;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("hudson.plugins.release.promotion.ReleasePromotionCondition")
//TODO Replace with original impl
public class ReleasePromotionCondition extends PromotionCondition {

}
