export function changeInputValue(input, value) {
    const nativeInputValueSetter = Object.getOwnPropertyDescriptor(
        window.HTMLInputElement.prototype,
        'value'
    ).set;
    nativeInputValueSetter.call(input, value);
    const event = new Event('input', {bubbles: true});
    event.simulated = true;
    input.dispatchEvent(event);
}

export function clickElement(element){
    const event = new Event('click', {bubbles: true});
    element.dispatchEvent(event);
}

