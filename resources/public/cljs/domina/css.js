goog.provide('domina.css');
goog.require('cljs.core');
goog.require('goog.dom.query');
goog.require('goog.dom');
goog.require('domina');
domina.css.root_element = (function root_element(){
return (goog.dom.getElementsByTagNameAndClass("html")[0]);
});
/**
* Returns content based on a css selector expression. Takes an optional content as a base; if none is given, uses the HTML element as a base.
*/
domina.css.sel = (function() {
var sel = null;
var sel__1 = (function (expr){
return sel.call(null,domina.css.root_element.call(null),expr);
});
var sel__2 = (function (base,expr){
if((void 0 === domina.css.t5107))
{goog.provide('domina.css.t5107');

/**
* @constructor
*/
domina.css.t5107 = (function (expr,base,sel,meta5108){
this.expr = expr;
this.base = base;
this.sel = sel;
this.meta5108 = meta5108;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
domina.css.t5107.cljs$lang$type = true;
domina.css.t5107.cljs$lang$ctorPrSeq = (function (this__752__auto__){
return cljs.core.list.call(null,"domina.css/t5107");
});
domina.css.t5107.cljs$lang$ctorPrWriter = (function (this__752__auto__,writer__753__auto__,opt__754__auto__){
return cljs.core._write.call(null,writer__753__auto__,"domina.css/t5107");
});
domina.css.t5107.prototype.domina$DomContent$ = true;
domina.css.t5107.prototype.domina$DomContent$nodes$arity$1 = (function (_){
var self__ = this;
return cljs.core.mapcat.call(null,(function (p1__5102_SHARP_){
return domina.normalize_seq.call(null,goog.dom.query(self__.expr,p1__5102_SHARP_));
}),domina.nodes.call(null,self__.base));
});
domina.css.t5107.prototype.domina$DomContent$single_node$arity$1 = (function (_){
var self__ = this;
return cljs.core.first.call(null,cljs.core.filter.call(null,cljs.core.complement.call(null,cljs.core.nil_QMARK_),cljs.core.mapcat.call(null,(function (p1__5103_SHARP_){
return domina.normalize_seq.call(null,goog.dom.query(self__.expr,p1__5103_SHARP_));
}),domina.nodes.call(null,self__.base))));
});
domina.css.t5107.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_5109){
var self__ = this;
return self__.meta5108;
});
domina.css.t5107.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_5109,meta5108__$1){
var self__ = this;
return (new domina.css.t5107(self__.expr,self__.base,self__.sel,meta5108__$1));
});
} else
{}
return (new domina.css.t5107(expr,base,sel,null));
});
sel = function(base,expr){
switch(arguments.length){
case 1:
return sel__1.call(this,base);
case 2:
return sel__2.call(this,base,expr);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
sel.cljs$lang$arity$1 = sel__1;
sel.cljs$lang$arity$2 = sel__2;
return sel;
})()
;
