import React, { useState, useRef, useEffect } from 'react';
import './Tooltip.css';

function Tooltip({ children, content, className = '', position = 'auto' }) {
  const [isVisible, setIsVisible] = useState(false);
  const [tooltipPosition, setTooltipPosition] = useState('bottom');
  const wrapperRef = useRef(null);
  const tooltipRef = useRef(null);

  useEffect(() => {
    if (isVisible && position === 'auto' && wrapperRef.current && tooltipRef.current) {
      const wrapperRect = wrapperRef.current.getBoundingClientRect();
      const tooltipRect = tooltipRef.current.getBoundingClientRect();
      const viewportHeight = window.innerHeight;
      
      // Verificar se há espaço suficiente abaixo
      const spaceBelow = viewportHeight - wrapperRect.bottom;
      const spaceAbove = wrapperRect.top;
      
      if (spaceBelow < 120 && spaceAbove > 120) {
        setTooltipPosition('above');
      } else {
        setTooltipPosition('bottom');
      }
    }
  }, [isVisible, position]);

  const getTooltipClassName = () => {
    let classes = 'tooltip-wrapper';
    if (className) classes += ` ${className}`;
    
    if (position !== 'auto') {
      classes += ` tooltip-${position}`;
    } else if (tooltipPosition !== 'bottom') {
      classes += ` tooltip-${tooltipPosition}`;
    }
    
    return classes;
  };

  return (
    <div 
      ref={wrapperRef}
      className={getTooltipClassName()}
      onMouseEnter={() => setIsVisible(true)}
      onMouseLeave={() => setIsVisible(false)}
    >
      {children}
      {isVisible && (
        <div ref={tooltipRef} className="tooltip-content">
          {content}
        </div>
      )}
    </div>
  );
}

export default Tooltip;