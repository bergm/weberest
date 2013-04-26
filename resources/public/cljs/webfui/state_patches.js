goog.provide('webfui.state_patches');
goog.require('cljs.core');
goog.require('clojure.set');
goog.require('clojure.set');
webfui.state_patches.patch = (function patch(state,diff){
if(cljs.core.truth_(diff))
{if(cljs.core.map_QMARK_.call(null,state))
{return cljs.core.into.call(null,cljs.core.ObjMap.EMPTY,(function (){var iter__12691__auto__ = (function iter__31308(s__31309){
return (new cljs.core.LazySeq(null,false,(function (){
var s__31309__$1 = s__31309;
while(true){
var temp__4092__auto__ = cljs.core.seq.call(null,s__31309__$1);
if(temp__4092__auto__)
{var xs__4579__auto__ = temp__4092__auto__;
var key = cljs.core.first.call(null,xs__4579__auto__);
return cljs.core.cons.call(null,cljs.core.PersistentVector.fromArray([key,(function (){var val1 = state.call(null,key);
var val2 = diff.call(null,key);
if(cljs.core.truth_((function (){var and__3941__auto__ = val1;
if(cljs.core.truth_(and__3941__auto__))
{return val2;
} else
{return and__3941__auto__;
}
})()))
{return patch.call(null,val1,val2);
} else
{if(cljs.core.contains_QMARK_.call(null,diff,key))
{return val2;
} else
{if("\uFDD0'else")
{return val1;
} else
{return null;
}
}
}
})()], true),iter__31308.call(null,cljs.core.rest.call(null,s__31309__$1)));
} else
{return null;
}
break;
}
}),null));
});
return iter__12691__auto__.call(null,clojure.set.union.call(null,cljs.core.set.call(null,cljs.core.keys.call(null,state)),cljs.core.set.call(null,cljs.core.keys.call(null,diff))));
})());
} else
{if(cljs.core.vector_QMARK_.call(null,state))
{if(cljs.core.map_QMARK_.call(null,diff))
{return cljs.core.vec.call(null,cljs.core.map_indexed.call(null,(function (index,item){
var temp__4090__auto__ = diff.call(null,index);
if(cljs.core.truth_(temp__4090__auto__))
{var d = temp__4090__auto__;
return patch.call(null,item,d);
} else
{return item;
}
}),state));
} else
{return diff;
}
} else
{if("\uFDD0'else")
{return diff;
} else
{return null;
}
}
}
} else
{return state;
}
});
