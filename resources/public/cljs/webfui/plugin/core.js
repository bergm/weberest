goog.provide('webfui.plugin.core');
goog.require('cljs.core');
webfui.plugin.core.active_plugins = cljs.core.atom.call(null,cljs.core.PersistentVector.EMPTY);
webfui.plugin.core.Plugin = {};
webfui.plugin.core.declare_events = (function declare_events(this$,body,dom_watchers,parsed_html){
if((function (){var and__3941__auto__ = this$;
if(and__3941__auto__)
{return this$.webfui$plugin$core$Plugin$declare_events$arity$4;
} else
{return and__3941__auto__;
}
})())
{return this$.webfui$plugin$core$Plugin$declare_events$arity$4(this$,body,dom_watchers,parsed_html);
} else
{var x__12594__auto__ = (((this$ == null))?null:this$);
return (function (){var or__3943__auto__ = (webfui.plugin.core.declare_events[goog.typeOf(x__12594__auto__)]);
if(or__3943__auto__)
{return or__3943__auto__;
} else
{var or__3943__auto____$1 = (webfui.plugin.core.declare_events["_"]);
if(or__3943__auto____$1)
{return or__3943__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Plugin.declare-events",this$);
}
}
})().call(null,this$,body,dom_watchers,parsed_html);
}
});
webfui.plugin.core.fix_dom = (function fix_dom(this$){
if((function (){var and__3941__auto__ = this$;
if(and__3941__auto__)
{return this$.webfui$plugin$core$Plugin$fix_dom$arity$1;
} else
{return and__3941__auto__;
}
})())
{return this$.webfui$plugin$core$Plugin$fix_dom$arity$1(this$);
} else
{var x__12594__auto__ = (((this$ == null))?null:this$);
return (function (){var or__3943__auto__ = (webfui.plugin.core.fix_dom[goog.typeOf(x__12594__auto__)]);
if(or__3943__auto__)
{return or__3943__auto__;
} else
{var or__3943__auto____$1 = (webfui.plugin.core.fix_dom["_"]);
if(or__3943__auto____$1)
{return or__3943__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Plugin.fix-dom",this$);
}
}
})().call(null,this$);
}
});
webfui.plugin.core.register_plugin = (function register_plugin(plugin){
return cljs.core.swap_BANG_.call(null,webfui.plugin.core.active_plugins,cljs.core.conj,plugin);
});
