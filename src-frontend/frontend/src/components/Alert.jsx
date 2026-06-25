import { useEffect, useState } from 'react';

export function Alert({ type = 'info', message, onClose }) {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    if (onClose) {
      const timer = setTimeout(() => {
        setIsVisible(false);
        onClose();
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [onClose]);

  if (!isVisible) return null;

  const bgColor = {
    info: 'bg-blue-900 border-blue-600',
    success: 'bg-green-900 border-green-600',
    error: 'bg-red-900 border-red-600',
    warning: 'bg-yellow-900 border-yellow-600',
  }[type] || 'bg-blue-900';

  return (
    <div
      className={`fixed top-4 left-1/2 transform -translate-x-1/2 w-full max-w-md mx-auto p-4 rounded-lg border-l-4 ${bgColor} shadow-lg z-50 flex items-center gap-3 animate-fade-in`}
      role="alert"
    >
      <div className="flex-shrink-0">
        {type === 'error' && (
          <svg className="w-6 h-6 text-red-400" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
          </svg>
        )}
        {type === 'success' && (
          <svg className="w-6 h-6 text-green-400" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
          </svg>
        )}
        {type === 'warning' && (
          <svg className="w-6 h-6 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M8.257 3.099c.765 0 1.53 1.135 1.53 2.304v1.061c0 .442.456.776.914.776h2.886c.457 0 .914-.333.914-.776V5.401c0-1.169.765-2.304 1.53-2.304H8.257zM12.23 6.559v1.125c0 .443-.457.777-.914.777h-1.828c-.457 0-.914-.333-.914-.777V6.559c0-.442.457-.776.914-.776h1.828c.457 0 .914.334.914.776z" clipRule="evenodd" />
          </svg>
        )}
        {type === 'info' && (
          <svg className="w-6 h-6 text-blue-400" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
          </svg>
        )}
      </div>
      <div className="flex-1">
        <p className="text-white font-medium">{message}</p>
      </div>
      <button
        onClick={() => setIsVisible(false)}
        className="text-white hover:text-gray-200 text-sm font-medium"
      >
        ✕
      </button>
    </div>
  );
}

// Exportación por defecto para uso directo
export default Alert;
