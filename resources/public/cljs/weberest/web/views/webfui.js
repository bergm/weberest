goog.provide('weberest.web.views.webfui');
goog.require('cljs.core');
goog.require('webfui.framework');
goog.require('cljs.reader');
goog.require('webfui.utilities');
goog.require('goog.net.XhrIo');
goog.require('cljs.reader');
goog.require('webfui.utilities');
goog.require('webfui.framework');
weberest.web.views.webfui.initial_state = cljs.core.ObjMap.fromObject(["\uFDD0'amount","\uFDD0'amount-decimal","\uFDD0'accumulator","\uFDD0'operation","\uFDD0'memory"],{"\uFDD0'amount":null,"\uFDD0'amount-decimal":null,"\uFDD0'accumulator":0,"\uFDD0'operation":null,"\uFDD0'memory":"\uFDD0'unknown"});
weberest.web.views.webfui.calculator_keys = cljs.core.PersistentVector.fromArray([cljs.core.PersistentVector.fromArray([cljs.core.PersistentVector.fromArray(["\uFDD0'ac","AC","\uFDD0'ac"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'ms","MS","\uFDD0'ms"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'mr","MR","\uFDD0'mr"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'div","\u00F7","\uFDD0'op"], true)], true),cljs.core.PersistentVector.fromArray([cljs.core.PersistentVector.fromArray(["\uFDD0'7","7","\uFDD0'num"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'8","8","\uFDD0'num"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'9","9","\uFDD0'num"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'mult","\u00D7","\uFDD0'op"], true)], true),cljs.core.PersistentVector.fromArray([cljs.core.PersistentVector.fromArray(["\uFDD0'4","4","\uFDD0'num"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'5","5","\uFDD0'num"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'6","6","\uFDD0'num"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'minus","-","\uFDD0'op"], true)], true),cljs.core.PersistentVector.fromArray([cljs.core.PersistentVector.fromArray(["\uFDD0'1","1","\uFDD0'num"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'2","2","\uFDD0'num"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'3","3","\uFDD0'num"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'plus","+","\uFDD0'op"], true)], true),cljs.core.PersistentVector.fromArray([cljs.core.PersistentVector.fromArray(["\uFDD0'period",".","\uFDD0'period"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'0","0","\uFDD0'num"], true),cljs.core.PersistentVector.fromArray(["\uFDD0'eq","=","\uFDD0'op"], true)], true)], true);
weberest.web.views.webfui.operations = cljs.core.ObjMap.fromObject(["\uFDD0'div","\uFDD0'mult","\uFDD0'minus","\uFDD0'plus","\uFDD0'eq"],{"\uFDD0'div":cljs.core._SLASH_,"\uFDD0'mult":cljs.core._STAR_,"\uFDD0'minus":cljs.core._,"\uFDD0'plus":cljs.core._PLUS_,"\uFDD0'eq":cljs.core.identity});
weberest.web.views.webfui.right_shorten = (function right_shorten(s){
return cljs.core.subs.call(null,s,0,(cljs.core.count.call(null,s) - 1));
});
weberest.web.views.webfui.format_accumulator = (function format_accumulator(accumulator){
var s = accumulator.toFixed(12);
while(true){
var G__30490 = cljs.core.last.call(null,s);
if(cljs.core._EQ_.call(null,".",G__30490))
{return weberest.web.views.webfui.right_shorten.call(null,s);
} else
{if(cljs.core._EQ_.call(null,"0",G__30490))
{{
var G__30491 = weberest.web.views.webfui.right_shorten.call(null,s);
s = G__30491;
continue;
}
} else
{if("\uFDD0'else")
{return s;
} else
{return null;
}
}
}
break;
}
});
weberest.web.views.webfui.check_overflow = (function check_overflow(s){
while(true){
if((function (){var and__3941__auto__ = (cljs.core.count.call(null,s) <= 12);
if(and__3941__auto__)
{return cljs.core.not_EQ_.call(null,cljs.core.last.call(null,s),".");
} else
{return and__3941__auto__;
}
})())
{return s;
} else
{if(cljs.core.truth_(cljs.core.some.call(null,cljs.core.partial.call(null,cljs.core._EQ_,"."),s)))
{{
var G__30492 = weberest.web.views.webfui.right_shorten.call(null,s);
s = G__30492;
continue;
}
} else
{if("\uFDD0'else")
{return "OVERFLOW";
} else
{return null;
}
}
}
break;
}
});
weberest.web.views.webfui.render_display = (function render_display(p__30493){
var map__30495 = p__30493;
var map__30495__$1 = ((cljs.core.seq_QMARK_.call(null,map__30495))?cljs.core.apply.call(null,cljs.core.hash_map,map__30495):map__30495);
var accumulator = cljs.core._lookup.call(null,map__30495__$1,"\uFDD0'accumulator",null);
var amount_decimal = cljs.core._lookup.call(null,map__30495__$1,"\uFDD0'amount-decimal",null);
var amount = cljs.core._lookup.call(null,map__30495__$1,"\uFDD0'amount",null);
return weberest.web.views.webfui.check_overflow.call(null,((cljs.core.not.call(null,amount))?weberest.web.views.webfui.format_accumulator.call(null,accumulator):(cljs.core.truth_(amount_decimal)?amount.toFixed(amount_decimal):(("\uFDD0'else")?[cljs.core.str(amount)].join(''):null))));
});
weberest.web.views.webfui.render_all = (function render_all(state){
return cljs.core.PersistentVector.fromArray(["\uFDD0'table",cljs.core.PersistentVector.fromArray(["\uFDD0'tbody",cljs.core.PersistentVector.fromArray(["\uFDD0'tr",cljs.core.PersistentVector.fromArray(["\uFDD0'td",cljs.core.ObjMap.fromObject(["\uFDD0'colspan"],{"\uFDD0'colspan":4}),cljs.core.PersistentVector.fromArray(["\uFDD0'div#display",weberest.web.views.webfui.render_display.call(null,state)], true)], true)], true),(function (){var iter__12691__auto__ = (function iter__30506(s__30507){
return (new cljs.core.LazySeq(null,false,(function (){
var s__30507__$1 = s__30507;
while(true){
var temp__4092__auto__ = cljs.core.seq.call(null,s__30507__$1);
if(temp__4092__auto__)
{var xs__4579__auto__ = temp__4092__auto__;
var row = cljs.core.first.call(null,xs__4579__auto__);
return cljs.core.cons.call(null,cljs.core.PersistentVector.fromArray(["\uFDD0'tr",(function (){var iter__12691__auto__ = ((function (row,xs__4579__auto__,temp__4092__auto__){
return (function iter__30512(s__30513){
return (new cljs.core.LazySeq(null,false,((function (row,xs__4579__auto__,temp__4092__auto__){
return (function (){
var s__30513__$1 = s__30513;
while(true){
var temp__4092__auto____$1 = cljs.core.seq.call(null,s__30513__$1);
if(temp__4092__auto____$1)
{var xs__4579__auto____$1 = temp__4092__auto____$1;
var vec__30515 = cljs.core.first.call(null,xs__4579__auto____$1);
var sym = cljs.core.nth.call(null,vec__30515,0,null);
var label = cljs.core.nth.call(null,vec__30515,1,null);
var mouse = cljs.core.nth.call(null,vec__30515,2,null);
return cljs.core.cons.call(null,cljs.core.PersistentVector.fromArray(["\uFDD0'td",cljs.core.ObjMap.fromObject(["\uFDD0'colspan"],{"\uFDD0'colspan":cljs.core.ObjMap.fromObject(["\uFDD0'eq"],{"\uFDD0'eq":2}).call(null,sym,1)}),cljs.core.PersistentVector.fromArray(["\uFDD0'div",cljs.core.ObjMap.fromObject(["\uFDD0'id","\uFDD0'mouse"],{"\uFDD0'id":sym,"\uFDD0'mouse":mouse}),label], true)], true),iter__30512.call(null,cljs.core.rest.call(null,s__30513__$1)));
} else
{return null;
}
break;
}
});})(row,xs__4579__auto__,temp__4092__auto__))
,null));
});})(row,xs__4579__auto__,temp__4092__auto__))
;
return iter__12691__auto__.call(null,row);
})()], true),iter__30506.call(null,cljs.core.rest.call(null,s__30507__$1)));
} else
{return null;
}
break;
}
}),null));
});
return iter__12691__auto__.call(null,weberest.web.views.webfui.calculator_keys);
})()], true)], true);
});
webfui.framework.add_mouse_watch_helper.call(null,"\uFDD0'num",(function (state,first_element,last_element){
if(cljs.core.truth_(webfui.utilities.clicked.call(null,first_element,last_element)))
{var map__30516 = state;
var map__30516__$1 = ((cljs.core.seq_QMARK_.call(null,map__30516))?cljs.core.apply.call(null,cljs.core.hash_map,map__30516):map__30516);
var amount_decimal = cljs.core._lookup.call(null,map__30516__$1,"\uFDD0'amount-decimal",null);
var amount = cljs.core._lookup.call(null,map__30516__$1,"\uFDD0'amount",null);
var digit = parseInt(cljs.core.name.call(null,webfui.utilities.get_attribute.call(null,first_element,"\uFDD0'id")));
if(cljs.core.truth_(amount_decimal))
{return cljs.core.ObjMap.fromObject(["\uFDD0'amount","\uFDD0'amount-decimal"],{"\uFDD0'amount":(amount + ((digit / 10) / cljs.core.apply.call(null,cljs.core._STAR_,cljs.core.repeat.call(null,amount_decimal,10)))),"\uFDD0'amount-decimal":(amount_decimal + 1)});
} else
{return cljs.core.ObjMap.fromObject(["\uFDD0'amount"],{"\uFDD0'amount":((amount * 10) + digit)});
}
} else
{return null;
}
}),"\uFDD0'full");
webfui.framework.add_mouse_watch_helper.call(null,"\uFDD0'op",(function (state,first_element,last_element){
if(cljs.core.truth_(webfui.utilities.clicked.call(null,first_element,last_element)))
{var map__30517 = state;
var map__30517__$1 = ((cljs.core.seq_QMARK_.call(null,map__30517))?cljs.core.apply.call(null,cljs.core.hash_map,map__30517):map__30517);
var accumulator = cljs.core._lookup.call(null,map__30517__$1,"\uFDD0'accumulator",null);
var operation = cljs.core._lookup.call(null,map__30517__$1,"\uFDD0'operation",null);
var amount = cljs.core._lookup.call(null,map__30517__$1,"\uFDD0'amount",null);
return cljs.core.ObjMap.fromObject(["\uFDD0'amount","\uFDD0'amount-decimal","\uFDD0'accumulator","\uFDD0'operation"],{"\uFDD0'amount":null,"\uFDD0'amount-decimal":null,"\uFDD0'accumulator":(cljs.core.truth_((function (){var and__3941__auto__ = amount;
if(cljs.core.truth_(and__3941__auto__))
{return operation;
} else
{return and__3941__auto__;
}
})())?weberest.web.views.webfui.operations.call(null,operation).call(null,accumulator,amount):(function (){var or__3943__auto__ = amount;
if(cljs.core.truth_(or__3943__auto__))
{return or__3943__auto__;
} else
{return accumulator;
}
})()),"\uFDD0'operation":webfui.utilities.get_attribute.call(null,first_element,"\uFDD0'id")});
} else
{return null;
}
}),"\uFDD0'full");
webfui.framework.add_mouse_watch_helper.call(null,"\uFDD0'period",(function (state,first_element,last_element){
if(cljs.core.truth_(webfui.utilities.clicked.call(null,first_element,last_element)))
{if(cljs.core.truth_((new cljs.core.Keyword("\uFDD0'amount-decimal")).call(null,state)))
{return null;
} else
{return cljs.core.ObjMap.fromObject(["\uFDD0'amount-decimal"],{"\uFDD0'amount-decimal":0});
}
} else
{return null;
}
}),"\uFDD0'full");
webfui.framework.add_mouse_watch_helper.call(null,"\uFDD0'ac",(function (state,first_element,last_element){
if(cljs.core.truth_(webfui.utilities.clicked.call(null,first_element,last_element)))
{return cljs.core.assoc.call(null,weberest.web.views.webfui.initial_state,"\uFDD0'memory",(new cljs.core.Keyword("\uFDD0'memory")).call(null,state));
} else
{return null;
}
}),"\uFDD0'full");
webfui.framework.add_mouse_watch_helper.call(null,"\uFDD0'ms",(function (state,first_element,last_element){
if(cljs.core.truth_(webfui.utilities.clicked.call(null,first_element,last_element)))
{var map__30518 = state;
var map__30518__$1 = ((cljs.core.seq_QMARK_.call(null,map__30518))?cljs.core.apply.call(null,cljs.core.hash_map,map__30518):map__30518);
var accumulator = cljs.core._lookup.call(null,map__30518__$1,"\uFDD0'accumulator",null);
var amount = cljs.core._lookup.call(null,map__30518__$1,"\uFDD0'amount",null);
return cljs.core.ObjMap.fromObject(["\uFDD0'memory"],{"\uFDD0'memory":(function (){var or__3943__auto__ = amount;
if(cljs.core.truth_(or__3943__auto__))
{return or__3943__auto__;
} else
{return accumulator;
}
})()});
} else
{return null;
}
}),"\uFDD0'full");
webfui.framework.add_mouse_watch_helper.call(null,"\uFDD0'mr",(function (state,first_element,last_element){
if(cljs.core.truth_(webfui.utilities.clicked.call(null,first_element,last_element)))
{var map__30519 = state;
var map__30519__$1 = ((cljs.core.seq_QMARK_.call(null,map__30519))?cljs.core.apply.call(null,cljs.core.hash_map,map__30519):map__30519);
var memory = cljs.core._lookup.call(null,map__30519__$1,"\uFDD0'memory",null);
return cljs.core.ObjMap.fromObject(["\uFDD0'amount"],{"\uFDD0'amount":memory});
} else
{return null;
}
}),"\uFDD0'full");
weberest.web.views.webfui.my_state = cljs.core.atom.call(null,weberest.web.views.webfui.initial_state);
weberest.web.views.webfui.send = (function send(safe_state,method,uri,fun){
return goog.net.XhrIo.send(uri,(function (event){
var response = event.target;
if(cljs.core.truth_(response.isSuccess()))
{return fun.call(null,response.getResponseText());
} else
{return cljs.core.reset_BANG_.call(null,weberest.web.views.webfui.my_state,safe_state);
}
}),cljs.core.name.call(null,method));
});
weberest.web.views.webfui.memory_loaded = (function memory_loaded(text){
var memory = cljs.reader.read_string.call(null,text);
return cljs.core.swap_BANG_.call(null,weberest.web.views.webfui.my_state,cljs.core.assoc,"\uFDD0'memory",memory,"\uFDD0'amount",memory);
});
weberest.web.views.webfui.memory_saved = (function memory_saved(){
return cljs.core.swap_BANG_.call(null,weberest.web.views.webfui.my_state,cljs.core.assoc,"\uFDD0'memory","\uFDD0'unknown");
});
cljs.core.add_watch.call(null,weberest.web.views.webfui.my_state,"\uFDD0'my-watch",(function (_,___$1,old,new$){
var map__30520 = new$;
var map__30520__$1 = ((cljs.core.seq_QMARK_.call(null,map__30520))?cljs.core.apply.call(null,cljs.core.hash_map,map__30520):map__30520);
var memory = cljs.core._lookup.call(null,map__30520__$1,"\uFDD0'memory",null);
var amount = cljs.core._lookup.call(null,map__30520__$1,"\uFDD0'amount",null);
if(cljs.core._EQ_.call(null,amount,"\uFDD0'unknown"))
{if(cljs.core._EQ_.call(null,(new cljs.core.Keyword("\uFDD0'amount")).call(null,old),"\uFDD0'unknown"))
{cljs.core.reset_BANG_.call(null,weberest.web.views.webfui.my_state,old);
} else
{weberest.web.views.webfui.send.call(null,old,"\uFDD0'get","memory",weberest.web.views.webfui.memory_loaded);
}
} else
{}
if(cljs.core.not_EQ_.call(null,memory,"\uFDD0'unknown"))
{if(cljs.core.not_EQ_.call(null,(new cljs.core.Keyword("\uFDD0'memory")).call(null,old),"\uFDD0'unknown"))
{return cljs.core.reset_BANG_.call(null,weberest.web.views.webfui.my_state,old);
} else
{return weberest.web.views.webfui.send.call(null,old,"\uFDD0'put",[cljs.core.str("memory/"),cljs.core.str(memory)].join(''),weberest.web.views.webfui.memory_saved);
}
} else
{return null;
}
}));
webfui.framework.launch_app.call(null,weberest.web.views.webfui.my_state,weberest.web.views.webfui.render_all);
